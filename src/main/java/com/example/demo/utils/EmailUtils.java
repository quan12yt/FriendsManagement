package com.example.demo.utils;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailUtils {

    public Set<String> getEmailsFromText(String text) {
        Set<String> listEmail = new HashSet<>();
        Matcher matcher = Pattern
                .compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}").matcher(text);
        while (matcher.find()) {
            listEmail.add(matcher.group());
        }
        return listEmail;
    }

}
