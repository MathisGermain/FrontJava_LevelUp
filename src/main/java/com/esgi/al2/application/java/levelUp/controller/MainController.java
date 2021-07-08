package com.esgi.al2.application.java.levelUp.controller;

import com.esgi.al2.application.java.levelUp.form.ElementList;
import com.esgi.al2.application.java.levelUp.form.ExerciceForm;
import com.esgi.al2.application.java.levelUp.form.LoginForm;
import com.esgi.al2.application.java.levelUp.model.Exercice;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    // Injectez (inject) via application.properties.
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
    public String postLogin(Model model, //
    @ModelAttribute("loginForm") LoginForm loginForm) throws Exception {

        String email = loginForm.getEmail();
        String password = loginForm.getPassword();
        System.out.println("Verification " + email);

        if (email != null && email.length() > 0 //
                && password != null && password.length() > 0) {


            HttpClient client = getHttpClient();
            HttpRequest request = getHttpRequest(apiLevelUpUrl + "users/signin");



            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();


            System.out.println(response.body());


            // Appel API pour vérifier les informations du User
            if(1==1){
                // Ajout de l'id user en session
                System.out.println("Ok");
                return "redirect:/index";
            }else{
                model.addAttribute("errorLoginInvalid", errorLoginInvalid);
                return "login";
            }


        }else{
            model.addAttribute("errorLoginEmpty", errorLoginEmpty);
            return "login";
        }
    }

    @RequestMapping(value = { "/exerciceList" }, method = RequestMethod.GET)
    public String showExerciceListPage(Model model) {

        List<Exercice> exercices = new ArrayList<Exercice>();

        Exercice ex = new Exercice();
        ex.setId(1);
        ex.setTitle("Test");
        ex.setContent("Lorem ipsum");

        exercices.add(ex);
        exercices.add(ex);
        exercices.add(ex);

        ExerciceForm exerciceForm = new ExerciceForm();


        //appel api pour récupérer la liste d'exercice non résolu
        model.addAttribute("exercices", exercices);
        model.addAttribute("exerciceForm",exerciceForm);

        return "exerciceList";
    }

    @RequestMapping(value = { "/solvedExerciceList" }, method = RequestMethod.GET)
    public String showSolvedExerciceListPage(Model model) {

        List<Exercice> exercices = new ArrayList<Exercice>();

        Exercice ex = new Exercice();
        ex.setId(1);
        ex.setTitle("Test");
        ex.setContent("Lorem ipsum");

        exercices.add(ex);
        exercices.add(ex);
        exercices.add(ex);
        exercices.add(ex);
        //appel api pour récupérer la liste d'exercice résolu


        model.addAttribute("solvedExercices", exercices);

        return "solvedExerciceList";
    }

    @RequestMapping(value = { "/exercice" }, params = { "id" } , method = RequestMethod.GET)
    public String showExercicePage(Model model, @RequestParam("id") Integer id) throws Exception {
        
        HttpClient client = getHttpClient();
        HttpRequest request = getHttpRequest(apiLevelUpUrl + "exercises/1");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Exercice exercice = mapper.readValue(response.body(), Exercice.class);

        ExerciceForm exerciceForm = new ExerciceForm();
        exerciceForm.setExerciceId(id);
        exerciceForm.setTitle(exercice.getTitle());
        exerciceForm.setStatement(exercice.getContent());

        model.addAttribute("exerciceForm", exerciceForm);


        return "exercice";
    }

    @RequestMapping(value = { "/exercice" }, method = RequestMethod.POST)
    public String postExercice(Model model, //
                            @ModelAttribute("exerciceForm") ExerciceForm exerciceForm) {

        String code = exerciceForm.getCode();
        Integer exerciceId = exerciceForm.getExerciceId();

        if (code != null && code.length() > 0) {

            // Appel API pour envoyer le code Utilisateur

            System.out.println("Ok");


            return "redirect:/exercice";
        }else{
            model.addAttribute("errorCodeEmpty", errorCodeEmpty);
            return "exercice";
        }
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





}