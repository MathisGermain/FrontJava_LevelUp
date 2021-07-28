package com.esgi.al2.application.java.levelUp.controller;

import com.esgi.al2.application.java.levelUp.form.ElementList;
import com.esgi.al2.application.java.levelUp.form.ExerciceForm;
import com.esgi.al2.application.java.levelUp.form.LoginForm;
import com.esgi.al2.application.java.levelUp.form.ResponseForm;
import com.esgi.al2.application.java.levelUp.model.Exercice;
import com.esgi.al2.application.java.levelUp.model.Response;
import com.esgi.al2.application.java.levelUp.model.ResponseApi;
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

    @Value("${api.levelUp.url}")
    private String apiLevelUpUrl;

    @Value("${error.not.sign.in}")
    private String errorNotSignIn;


    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model,HttpSession session) throws Exception {

        if(!AuthController.checkSignIn(session)){
            LoginForm loginForm = new LoginForm();
            model.addAttribute("loginForm", loginForm);
            model.addAttribute("error",errorNotSignIn);
            return "login";
        }

        model.addAttribute("message", message);

        return "index";
    }

    public static HttpClient getHttpClient(){
        HttpClient client = HttpClient.newHttpClient();
        return client;
    }

    public static HttpRequest getHttpRequest(String uri) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();

        return request;
    }

    public static HttpRequest getHttpRequestWithAccessToken(String uri, String token) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization" , ("Bearer " + token))
                .build();
        return request;
    }


    public static HttpResponse<String> doGet(String url) throws Exception{
        HttpClient client = getHttpClient();
        HttpRequest request = getHttpRequest(url);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public static HttpResponse<String> doGetWithAccessToken(String url, String token) throws Exception{
        HttpClient client = getHttpClient();
        HttpRequest request = getHttpRequestWithAccessToken(url,token);
        System.out.println("Requete http post + token : " + request);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public static String doPostWithAccessToken(String urlPath, String json, String token) throws Exception{
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

    public static String doPost(String urlPath, String json) throws Exception{
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