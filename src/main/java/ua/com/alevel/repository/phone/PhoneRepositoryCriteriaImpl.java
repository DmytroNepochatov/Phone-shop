package ua.com.alevel.repository.phone;

import org.springframework.stereotype.Repository;
import ua.com.alevel.model.phone.Phone;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import static org.apache.commons.lang.NumberUtils.isNumber;

@Repository("phoneRepositoryCriteriaImpl")
public class PhoneRepositoryCriteriaImpl implements PhoneRepositoryCriteria {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Phone> filterPhones(String[] params, int page, int needPhones) {
        String sql = "select * from public.phone where phone.id in (\n" +
                "   select SPLIT_PART(STRING_AGG(phone.id, ','), ',', 1)\n" +
                "   from public.phone as phone where phone.check_id is null";
        String lastPartSql = "group by phone.brand_id, phone.name, phone.series, \n" +
                "   phone.amount_of_built_in_memory, phone.amount_of_ram\n" +
                ")";

        StringBuilder builder = new StringBuilder(sql);

        for (int i = 0; i < params.length; i++) {
            String[] pair = params[i].split("=");

            String value;
            if (isNumber(pair[1])) {
                value = " and " + pair[0] + " = " + pair[1];
            }
            else {
                value = " and " + pair[0] + " = " + "\'" + pair[1] + "\'";
            }

            builder.append(value);

            for (int j = i + 1; j < params.length; j++) {
                String[] pairOr = params[j].split("=");

                if (pair[0].equals(pairOr[0])) {
                    String valueOr;

                    if (isNumber(pairOr[1])) {
                        valueOr = " or " + pairOr[0] + " = " + pairOr[1];
                    }
                    else {
                        valueOr = " or " + pairOr[0] + " = " + "\'" + pairOr[1] + "\'";
                    }

                    builder.append(valueOr);
                }
                else {
                    i = j - 1;
                    break;
                }
            }
        }

        builder.append(lastPartSql);

        return em.createNativeQuery(builder.toString(), Phone.class)
                .getResultList();
    }
}