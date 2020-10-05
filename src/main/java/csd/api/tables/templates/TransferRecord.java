package csd.api.tables.templates;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TransferRecord {
    private int from_account;
    private int to_account;
    private double amount;
}
