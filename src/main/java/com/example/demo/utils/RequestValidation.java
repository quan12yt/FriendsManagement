package com.example.demo.utils;

import com.example.demo.payload.AddAndGetCommonRequest;
import com.example.demo.payload.SubscribeAndBlockRequest;

public class RequestValidation {

    public static String checkAddAndSubscribeRequest(AddAndGetCommonRequest request) {
        if (request.getFriends() == null || request == null) {
            return "Invalid request";
        }
        if (request.getFriends().size() != 2) {
            return "Must contains 2 emails";
        }
        if (!EmailValidation.isEmail(request.getFriends().get(0))
                || !EmailValidation.isEmail(request.getFriends().get(1))) {
            return "Invalid email";
        }
        if (request.getFriends().get(0).equals(request.getFriends().get(1))) {
            return "Same email error ";
        }
        return "";
    }

    public static String checkSubscribeAndBlockRequest(SubscribeAndBlockRequest request) {
        if (request.getRequester() == null || request.getTarget() == null) {
            return "Invalid Request";
        }
        if (!EmailValidation.isEmail(request.getRequester())
                || !EmailValidation.isEmail(request.getTarget())) {
            return "Invalid requester or target email";
        }
        if (request.getRequester().equals(request.getTarget())) {
            return "Same email error ";
        }
        return "";
    }

}
