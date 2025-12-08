package org.example;

import org.example.db_service.User;
import org.example.DAO.UserRepository;

import java.sql.SQLException;


public class Main {
    public static void main(String[] args) throws SQLException {
        User user = new User("example123@gmail.com", "sasun", "14532", "admin" );
        User user1 = new User("example133@gmail.com", "saкun", "177532", "admin" );

        UserRepository userRepository = new UserRepository();
        userRepository.insert(user);
        userRepository.insert(user1);

    }
}