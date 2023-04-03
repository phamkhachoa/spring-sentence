package com.csv.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Translation {
    private String id;
    private String text;
    private String audioUrl;
    private String translateId;
    private String translateText;
}
