package vixen_sms_control;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sequence{
    @JsonProperty("Name") 
    public String name;
    @JsonProperty("FileName") 
    public String fileName;
}