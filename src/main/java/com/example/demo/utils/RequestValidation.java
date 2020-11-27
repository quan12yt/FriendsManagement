package com.example.demo.utils;

import com.example.demo.dto.request.AddAndGetCommonRequest;
import com.example.demo.dto.request.RetrieveRequest;
import com.example.demo.dto.request.SubscribeAndBlockRequest;

public class RequestValidation {

    public static String checkAddAndGetCommonRequest(AddAndGetCommonRequest request) {
        if (request == null || request.getFriends() == null) {
            return "Invalid request";
        }
        if (request.getFriends().size() != 2) {
            return "Must contains 2 emails";
        }
        if (request.getFriends().get(0) ==null || request.getFriends().get(1) ==null) {
            return "Invalid request";
        }
        if (!EmailValidation.isEmail(request.getFriends().get(0))
                || !EmailValidation.isEmail(request.getFriends().get(1))) {
            return "Invalid email";
        }
        if (request.getFriends().get(0).equals(request.getFriends().get(1))) {
            return "Same email error";
        }
        return "";
    }

    public static String checkSubscribeAndBlockRequest(SubscribeAndBlockRequest request) {
        if (request == null || request.getRequester() == null || request.getTarget() == null) {
            return "Invalid request";
        }
        if (!EmailValidation.isEmail(request.getRequester())
                || !EmailValidation.isEmail(request.getTarget())) {
            return "Invalid requester or target email";
        }
        if (request.getRequester().equals(request.getTarget())) {
            return "Same email error";
        }
        return "";
    }

    public static String checkRetrieveRequest(RetrieveRequest request) {
        if (request == null || request.getSender() == null || request.getText() == null) {
            return "Invalid request";
        }
        if (!EmailValidation.isEmail(request.getSender())) {
            return "Invalid sender email";
        }
        return "";
    }

}
