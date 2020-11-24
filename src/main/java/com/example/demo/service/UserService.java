package com.example.demo.service;

import com.example.demo.exception.DataNotFoundException;
import com.example.demo.model.Users;
import com.example.demo.repository.UserRepository;
import com.example.demo.securiry.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<Users> users = userRepository.findByUserName(s);
        if(users.isEmpty()){
            throw new DataNotFoundException("Cant find any User for UserName : "+s);
        }
        return new CustomUserDetails(users.get());
    }

    public UserDetails loadUserByUserID(Long userId) throws UsernameNotFoundException {
        Optional<Users> users = userRepository.findById(userId);
        if(users.isEmpty()){
            throw new DataNotFoundException("Cant find any User for UserID : "+userId);
        }
        return new CustomUserDetails(users.get());
    }
}
