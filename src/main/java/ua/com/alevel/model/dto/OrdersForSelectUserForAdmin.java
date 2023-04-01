package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.com.alevel.model.check.ClientCheck;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrdersForSelectUserForAdmin implements Comparable<OrdersForSelectUserForAdmin> {
    private ClientCheck check;
    private String dates;
    private String datesClosed;
    private int totalPrices;
    private boolean flag;

    @Override
    public int compareTo(OrdersForSelectUserForAdmin order) {
        if (order.flag) {
            return 1;
        }
        if (flag) {
            return -1;
        }
        return 0;
    }
}
