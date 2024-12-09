package org.poo.bank.currency;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class Exchange {
    private String from;
    private String to;
    private double rate;
    private int timestamp;
}
