/**
 * Copyright 
 */
package com.liangshan.jianjian.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.liangshan.jianjian.types.Venue;


/**
 * @date 
 * @author
 */
public class VenueParser extends AbstractParser<Venue> {
 
    @Override
    public Venue parse(JSONObject json) throws JSONException {

        Venue obj = new Venue();
        if (json.has("addr")) {
            obj.setAddress(json.getString("addr"));
        } 
        /*
        if (json.has("checkins")) {
            obj.setCheckins(
                new GroupParser(
                    new CheckinParser()).parse(json.getJSONArray("checkins")));
        } */
        if (json.has("city")) {
            obj.setCity(json.getString("city"));
        } 
        if (json.has("cityid")) {
            obj.setCityid(json.getString("cityid"));
        } 
        if (json.has("crossstreet")) {
            obj.setCrossstreet(json.getString("crossstreet"));
        } 
        if (json.has("distance")) {
            obj.setDistance(json.getString("distance"));
        } 
        if (json.has("lat")) {
            obj.setGeolat(json.getString("lat"));
        }
        if (json.has("lon")) {
            obj.setGeolong(json.getString("lon"));
        } 
        if (json.has("hasTodo")) {
        	obj.setHasTodo(json.getBoolean("hasTodo"));
        }
        if (json.has("guid")) {
            obj.setId(json.getString("guid"));
        } 
        if (json.has("name")) {
            obj.setName(json.getString("name"));
        } 
        if (json.has("tel")) {
            obj.setPhone(json.getString("tel"));
        } 
        /*
        if (json.has("primarycategory")) {
             obj.setCategory(new CategoryParser().parse(json.getJSONObject("primarycategory")));
        } 
        
        if (json.has("specials")) {
            obj.setSpecials(
                new GroupParser(
                    new SpecialParser()).parse(json.getJSONArray("specials")));
        } 
        */
        if (json.has("province")) {
            obj.setState(json.getString("province"));
        } 
        /*
        if (json.has("stats")) {
             obj.setStats(new StatsParser().parse(json.getJSONObject("stats")));
        } 
        if (json.has("tags")) {
            obj.setTags(
                new Tags(StringArrayParser.parse(json.getJSONArray("tags"))));
        }
        if (json.has("tips")) {
            obj.setTips(
                new GroupParser(
                    new TipParser()).parse(json.getJSONArray("tips")));
        } 
        if (json.has("todos")) {
            obj.setTodos(
                new GroupParser(
                    new TodoParser()).parse(json.getJSONArray("todos")));
        } 
        if (json.has("twitter")) {
            obj.setTwitter(json.getString("twitter"));
        } 
        
        if (json.has("zip")) {
            obj.setZip(json.getString("zip"));
        }
        */

        return obj;
    }
}