package se.ecutb.erik;

import se.ecutb.erik.data.Database;
import se.ecutb.erik.data.PersonDaoJDBC;
import se.ecutb.erik.entity.Person;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws SQLException {
        PersonDaoJDBC dao = new PersonDaoJDBC();


    }
}
