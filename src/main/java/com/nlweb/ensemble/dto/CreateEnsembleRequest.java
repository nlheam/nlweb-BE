package com.nlweb.ensemble.dto;

import lombok.*;

@Data
public class CreateEnsembleRequest {

    private Long eventId;
    private String artist;
    private String title;
    private String notes;
    private String vocal;
    private String leadGuitar;
    private String rhythmGuitar;
    private String bass;
    private String drums;
    private String piano;
    private String synth;
    private String etc;

}
