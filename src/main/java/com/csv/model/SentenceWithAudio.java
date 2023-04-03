package com.csv.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SentenceWithAudio {
    private String sentenceId;
    private String username;
    private String license;
    private String attributionUrl;
}
