package ua.com.webservice.repository.phone;

import org.springframework.stereotype.Repository;
import ua.com.webservice.model.phone.PhoneInstance;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import static org.apache.commons.lang.NumberUtils.isNumber;

@Repository("phoneInstanceRepositoryCriteriaImpl")
public class PhoneInstanceRepositoryCriteriaImpl implements PhoneInstanceRepositoryCriteria {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<PhoneInstance> filterPhones(String[] params, int needPhones) {
        String sql = "select * from public.phone_instance where phone_instance.id in (\n" +
                "select SPLIT_PART(STRING_AGG(phone_instance.id, ','), ',', 1)\n" +
                "from public.phone_instance as phone_instance, public.phone as phone, public.phone_description as phone_description where phone_instance.check_id is null\n" +
                "and phone_instance.phone_id=phone.id and phone.phone_description_id=phone_description.id\n";
        String lastPartSql = "group by phone_description.brand_id, phone_description.name, phone_description.series, \n" +
                "phone.amount_of_built_in_memory, phone.amount_of_ram\n" +
                ")";

        StringBuilder builder = new StringBuilder(sql);

        for (int i = 0; i < params.length; i++) {
            String[] pair = params[i].split("=");

            String value;
            if (isNumber(pair[1])) {
                value = " and " + pair[0] + " = " + pair[1] + " ";
            }
            else {
                value = " and " + pair[0] + " = " + "\'" + pair[1] + "\' ";
            }

            String checkStr = value.replaceFirst("and", "").trim();
            if (!builder.toString().contains(checkStr)) {
                builder.append(value);

                if (builder.toString().contains(" or ")) {
                    int index = -1;
                    int counter = 0;
                    while ((index = builder.indexOf(" or ", index + 1)) != -1) {
                        counter++;
                    }

                    int pos = 0;
                    while (pos != counter) {
                        List<Integer> indexes = new ArrayList<>();
                        int ind = -1;
                        while ((ind = builder.indexOf(" or ", ind + 1)) != -1) {
                            indexes.add(ind);
                        }

                        builder.insert(indexes.get(pos), value);
                        pos++;
                    }
                }
            }

            for (int j = i + 1; j < params.length; j++) {
                String[] pairOr = params[j].split("=");

                if (pair[0].equals(pairOr[0])) {
                    String valueOr;

                    if (isNumber(pairOr[1])) {
                        valueOr = pairOr[0] + " = " + pairOr[1];
                    }
                    else {
                        valueOr = pairOr[0] + " = " + "\'" + pairOr[1] + "\'";
                    }

                    String valueForOr = " or phone_instance.check_id is null\n" +
                            "and phone_instance.phone_id=phone.id and phone.phone_description_id=phone_description.id\n" +
                            "\tand " + valueOr + " ";
                    builder.append(valueForOr);
                }
                else {
                    i = j - 1;
                    break;
                }
            }
        }

        builder.append(lastPartSql);

        return em.createNativeQuery(builder.toString(), PhoneInstance.class)
                .getResultList();
    }
}