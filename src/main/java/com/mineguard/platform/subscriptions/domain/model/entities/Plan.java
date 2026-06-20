package com.mineguard.platform.subscriptions.domain.model.entities;
import com.mineguard.platform.subscriptions.domain.model.valueobjects.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Getter
@Setter
@NoArgsConstructor
public class Plan {
    private Long id;
    private String name;
    private Money price;
    private String billingCycle;
    public Plan(String name, Money price, String billingCycle) {
        this.name = name;
        this.price = price;
        this.billingCycle = billingCycle;
    }
    public int billingCycleDays() {
        if (billingCycle == null || billingCycle.isBlank()) return 30;
        Matcher matcher = Pattern.compile("(\\d+)").matcher(billingCycle);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 30;
    }
}
