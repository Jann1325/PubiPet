package io.github.jann1325.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LookupDTO {

    private String lookupKey;

    private String lookupValue;

}
