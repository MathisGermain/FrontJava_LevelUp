package com.esgi.al2.application.java.levelUp.controller;

import com.esgi.al2.application.java.levelUp.form.ElementList;
import com.esgi.al2.application.java.levelUp.form.ExerciceForm;
import com.esgi.al2.application.java.levelUp.form.LoginForm;
import com.esgi.al2.application.java.levelUp.model.Exercice;
import com.esgi.al2.application.java.levelUp.model.Response;
import com.esgi.al2.application.java.levelUp.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class MainController {

    @Value("${welcome.message}")
    private String message;

    @Value("${error.empty.login.input}")
    private String errorLoginEmpty;

    @Value("${error.login.invalid}")
    private String errorLoginInvalid;

    @Value("${error.empty.code.input}")
    private String errorCodeEmpty;

    @Value("${api.levelUp.url}")
    private String apiLevelUpUrl;





    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model) {

        model.addAttribute("message", message);

        return "index";
    }

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
                model.addAttribute("errorLoginInvalid", errorLoginInvalid);
                return "login";
            }
        }else{
            model.addAttribute("errorLoginEmpty", errorLoginEmpty);
            return "login";
        }
    }

    @RequestMapping(value = { "/exerciceList" }, method = RequestMethod.GET)
    public String showExerciceListPage(Model model, HttpServletRequest request,HttpSession session){

        List<Exercice> exercices;
        User sameObject = (User) session.getAttribute("user");
        ExerciceForm exerciceForm = new ExerciceForm();
        try{
            HttpResponse<String> response = doGetWithAccessToken(apiLevelUpUrl + "exercises/all",session.getAttribute("accessToken").toString());
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            exercices = Arrays.asList(mapper.readValue(response.body(), Exercice[].class));
        }catch (Exception e){
            return "error";
        }
        model.addAttribute("exercices", exercices);
        model.addAttribute("exerciceForm",exerciceForm);

        return "exerciceList";
    }

    @RequestMapping(value = { "/solvedExerciceList" }, method = RequestMethod.GET)
    public String showSolvedExerciceListPage(Model model, HttpServletRequest request,HttpSession session){

        List<Exercice> exercices;
        User sameObject = (User) session.getAttribute("user");
        ExerciceForm exerciceForm = new ExerciceForm();
        try{
            HttpResponse<String> response = doGetWithAccessToken(apiLevelUpUrl + "exercises/all",session.getAttribute("accessToken").toString());
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            exercices = Arrays.asList(mapper.readValue(response.body(), Exercice[].class));
        }catch (Exception e){
            return "error";
        }
        model.addAttribute("solvedExercices", exercices);

        return "solvedExerciceList";
    }

    @RequestMapping(value = { "/exercice" }, params = { "id" } , method = RequestMethod.GET)
    public String showExercicePage(Model model, HttpSession session , @RequestParam("id") Integer id){

        ExerciceForm exerciceForm = new ExerciceForm();
        try {
            HttpResponse<String> response = doGetWithAccessToken(apiLevelUpUrl + "exercises/" + id , session.getAttribute("accessToken").toString());
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Exercice exercice = mapper.readValue(response.body(), Exercice.class);
            exerciceForm.setExerciceId(id);
            exerciceForm.setTitle(exercice.getTitle());
            exerciceForm.setStatement(exercice.getContent());
        }catch (Exception e){
            return "error";
        }

        model.addAttribute("exerciceForm", exerciceForm);
        return "exercice";
    }

    @RequestMapping(value = { "/exercice" }, method = RequestMethod.POST)
    public String postExercice(Model model, HttpSession session ,
                            @ModelAttribute("exerciceForm") ExerciceForm exerciceForm) throws Exception {


        if (exerciceForm.getCode() != null && exerciceForm.getCode().length() > 0) {
            try {
                User user = (User) session.getAttribute("user");
                Response response = new Response(user.getId().toString(),exerciceForm.getExerciceId().toString(),exerciceForm.getCode());
                System.out.println("Langage : " + exerciceForm.getLangage());
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(response);
                String responseHttp = doPostWithAccessToken(apiLevelUpUrl + "responses/send-" + exerciceForm.getLangage(),json,session.getAttribute("accessToken").toString());

                model.addAttribute("status", responseHttp);
                return "redirect:/exercice?id="+exerciceForm.getExerciceId();

            }catch (Exception e){
                return "error";
            }
        }else{
            model.addAttribute("errorCodeEmpty", errorCodeEmpty);
            return "redirect:/exercice?id="+exerciceForm.getExerciceId();
        }
    }

    @RequestMapping(value = { "/usersResponsesList" }, params = { "id" } , method = RequestMethod.GET)
    public String showUsersResponsesList(Model model, @RequestParam("id") Integer id){

        ExerciceForm exerciceForm = new ExerciceForm();

        try {
            HttpClient client = getHttpClient();
            HttpRequest request = getHttpRequest(apiLevelUpUrl + "usersResponsesList/" + id);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Exercice exercice = mapper.readValue(response.body(), Exercice.class);
            exerciceForm.setExerciceId(id);
            exerciceForm.setTitle(exercice.getTitle());
            exerciceForm.setStatement(exercice.getContent());
        }catch (Exception e){
            return "error";
        }

        model.addAttribute("exerciceForm", exerciceForm);

        return "exercice";
    }

    public HttpClient getHttpClient(){
        HttpClient client = HttpClient.newHttpClient();
        return client;
    }

    public HttpRequest getHttpRequest(String uri) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();

        return request;
    }

    public HttpRequest getHttpRequestWithAccessToken(String uri, String token) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization" , ("Bearer " + token))
                .build();
        return request;
    }

    public User sendLogin(LoginForm loginForm) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(loginForm);

        String responseUser = doPost(apiLevelUpUrl + "users/signin",json);
        ObjectMapper mapperResult = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        User user = mapperResult.readValue(responseUser, User.class);

        String response = doPost(apiLevelUpUrl + "auth/login",json);

        return user;
    }

    public String generateAccessToken(LoginForm loginForm , Integer userId) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(loginForm);
        System.out.println("ResultingJSONstring = " + json);
        String response = doPost(apiLevelUpUrl + "auth/login",json);
        HttpResponse<String> accessToken = doGet(apiLevelUpUrl + "auth/token/" + userId);

        System.out.println("AccessToken : " + accessToken.body());

        return accessToken.body();
    }

    public  HttpResponse<String> doGet(String url) throws Exception{
        HttpClient client = getHttpClient();
        HttpRequest request = getHttpRequest(url);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public  HttpResponse<String> doGetWithAccessToken(String url, String token) throws Exception{
        HttpClient client = getHttpClient();
        HttpRequest request = getHttpRequestWithAccessToken(url,token);
        System.out.println("Requete http post + token : " + request);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public String doPostWithAccessToken(String urlPath, String json, String token) throws Exception{
        URL url = new URL(urlPath);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization" , ("Bearer " + token));
        con.setDoOutput(true);
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
        return sb.toString();

    }

    public String doPost(String urlPath, String json) throws Exception{
        URL url = new URL(urlPath);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
        return sb.toString();

    }

    public void sendExerciseResponse(ExerciceForm exerciceForm) throws Exception{
        // form parameters
        HttpClient httpClient = getHttpClient();
        Map<Object, Object> data = new HashMap<>();
        data.put("idExercice", exerciceForm.getExerciceId());
        data.put("idUser", "1"); //récupération en session
        data.put("code", exerciceForm.getCode());

        HttpRequest request = HttpRequest.newBuilder()
                .POST(buildFormDataFromMap(data))
                .uri(URI.create(apiLevelUpUrl + "responses/send"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // print status code
        //System.out.println(response.statusCode());

        // print response body
        //System.out.println(response.body());
    }

    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        System.out.println(builder.toString());
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }







}