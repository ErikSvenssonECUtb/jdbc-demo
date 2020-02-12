package se.ecutb.erik.data;

import se.ecutb.erik.entity.Person;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

import static se.ecutb.erik.data.Database.getConnection;

public class PersonDaoJDBC {

    private static final String INSERT =
            "INSERT INTO person(firstname,lastname,email,birthdate) VALUES(?,?,?,?)";

    private static final String FIND_BY_ID =
            "SELECT * FROM person WHERE person_id = ?";

    /*
        UPDATE table_name
        SET column1 = value1, column2 = value2, ...
        WHERE condition;
     */
    private static final String UPDATE_PERSON =
            "UPDATE person SET firstname = ?, lastname = ?, email = ?, birthdate = ? WHERE person_id = ?";

    /*
        DELETE FROM table_name WHERE condition;
     */
    private static final String DELETE_PERSON =
            "DELETE FROM person WHERE person_id = ?";


    public Person create(Person person){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet keySet = null;
        try{
            connection = getConnection();
            statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, person.getFirstName()); //(?,?,?,?) -> (Nisse, ?, ?, ?)
            statement.setString(2, person.getLastName());  //(Nisse,?,?,?) -> (Nisse, Nilsson, ?, ?)
            statement.setString(3,person.getEmail());      //(Nisse,Nilsson,?,?) -> (Nisse, Nilsson, Nisse@gmail.com, ?)
            statement.setObject(4, person.getBirthDate()); //(Nisse,Nilsson,Nisse@gmail.com,?) -> (Nisse, Nilsson, Nisse@gmail.com, 1980-01-01)
            statement.execute();
            keySet = statement.getGeneratedKeys();
            while(keySet.next()){
                person = new Person(
                        keySet.getInt(1),
                        person.getFirstName(),
                        person.getLastName(),
                        person.getEmail(),
                        person.getBirthDate()
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try{
                if(keySet != null){
                    keySet.close();
                }
                if(statement != null){
                    statement.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return person;
    }

    private PreparedStatement create_findById(Connection connection, int personId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(FIND_BY_ID);
        statement.setInt(1,personId);
        return statement;
    }

    private Person createPersonFromResultSet(ResultSet resultSet) throws SQLException {
        return new Person(
                resultSet.getInt("person_id"),
                resultSet.getString("firstname"),
                resultSet.getString("lastname"),
                resultSet.getString("email"),
                resultSet.getObject("birthdate", LocalDate.class)
        );
    }

    public Optional<Person> findByPersonId(int personId){
        Optional<Person> optional = Optional.empty();

        try(
                Connection connection = getConnection();
                PreparedStatement statement = create_findById(connection, personId);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while(resultSet.next()){
                optional = Optional.of(createPersonFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return optional;
    }

    /*                            1             2          3              4                   5
    UPDATE person SET firstname = ?, lastname = ?, email = ?, birthdate = ? WHERE person_id = ?
     */
    public Person update(Person person){
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE_PERSON)
        ) {
            statement.setString(1, person.getFirstName());
            statement.setString(2, person.getLastName());
            statement.setString(3, person.getEmail());
            statement.setObject(4, person.getBirthDate());
            statement.setInt(5, person.getPersonId());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return person;
    }

    public boolean delete(int personId){
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE_PERSON)
        ){
            statement.setInt(1,personId);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return !findByPersonId(personId).isPresent();
    }
}















