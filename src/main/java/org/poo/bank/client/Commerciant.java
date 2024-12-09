package org.poo.bank.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class Commerciant {
    private int id;
    private String description;
    private List<String> commerciants;
}
