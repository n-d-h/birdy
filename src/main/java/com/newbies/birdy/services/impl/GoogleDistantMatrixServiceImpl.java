package com.newbies.birdy.services.impl;

import com.newbies.birdy.services.GoogleDistantMatrixService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class GoogleDistantMatrixServiceImpl implements GoogleDistantMatrixService {
    private static final String API_KEY = "AIzaSyCdITJ7sBi_3oK-kthCzKwKzPqEFsNlJu4";

    @Override
    public Long getData(String source, String destination) throws Exception {
        source = URLEncoder.encode(source, "UTF-8");
        destination = URLEncoder.encode(destination, "UTF-8");
        var url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + source + "&destinations=" + destination + "&units=metric" + "&key=" + API_KEY;
        var request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        Long distance = -1L;
        distance = parse(response);
//        System.out.println(response);
        return distance;

    }

    public static Long parse(String response){
        long distance = -1L;
        //parsing json data and updating data
        {
            try {
                JSONParser jp = new JSONParser();
                JSONObject jo = (JSONObject) jp.parse(response);
                JSONArray ja = (JSONArray) jo.get("rows");
                jo = (JSONObject) ja.get(0);
                ja = (JSONArray) jo.get("elements");
                jo = (JSONObject) ja.get(0);
                JSONObject je = (JSONObject) jo.get("distance");
                distance = (Long) je.get("value");

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return distance;
    }

}
