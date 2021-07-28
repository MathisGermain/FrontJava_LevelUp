package com.esgi.al2.application.java.levelUp.controller;

import com.esgi.al2.application.java.levelUp.form.LoginForm;
import com.esgi.al2.application.java.levelUp.model.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.http.HttpResponse;

@Controller
public class AuthController {

    @Value("${api.levelUp.url}")
    private String apiLevelUpUrl;

    @Value("${error.login.invalid}")
    private String errorLoginInvalid;

    @Value("${error.empty.login.input}")
    private String errorLoginEmpty;

    @RequestMapping(value = { "/login" }, method = RequestMethod.GET)
    public String showLoginPage(Model model) throws Exception {

        LoginForm loginForm = new LoginForm();
        model.addAttribute("loginForm", loginForm);

        return "login";
    }

    @RequestMapping(value = { "/login" }, method = RequestMethod.POST)
    public String postLogin(Model model, HttpServletRequest request,//
                            @ModelAttribute("loginForm") LoginForm loginForm) throws Exception {

        String email = loginForm.getEmail();
        String password = loginForm.getPassword();

        if (email != null && email.length() > 0 //
                && password != null && password.length() > 0) {
            try{
                User user = sendLogin(loginForm);
                String accessToken = generateAccessToken(loginForm,user.getId());
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("accessToken" , accessToken);
                return "redirect:/index";
            }catch (Exception e){
                model.addAttribute("error", errorLoginInvalid);
                return "login";
            }
        }else{
            model.addAttribute("error", errorLoginEmpty);
            return "login";
        }
    }

    public String generateAccessToken(LoginForm loginForm , Integer userId) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(loginForm);
        System.out.println("ResultingJSONstring = " + json);
        String response = MainController.doPost(apiLevelUpUrl + "auth/login",json);
        HttpResponse<String> accessToken = MainController.doGet(apiLevelUpUrl + "auth/token/" + userId);

        System.out.println("AccessToken : " + accessToken.body());

        return accessToken.body();
    }

    public User sendLogin(LoginForm loginForm) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(loginForm);

        String responseUser = MainController.doPost(apiLevelUpUrl + "users/signin",json);
        ObjectMapper mapperResult = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        User user = mapperResult.readValue(responseUser, User.class);

        String response = MainController.doPost(apiLevelUpUrl + "auth/login",json);

        return user;
    }

    public static boolean checkSignIn(HttpSession session) throws Exception{
        if(session.getAttribute("accessToken")== null){
            return false;
        }else{
            return true;
        }
    }



}
