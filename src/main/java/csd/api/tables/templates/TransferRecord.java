package csd.api.tables.templates;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TransferRecord {
    private int from;
    private int to;
    private double amount;
}
