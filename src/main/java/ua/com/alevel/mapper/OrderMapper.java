package ua.com.alevel.mapper;

import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.dto.*;
import ua.com.alevel.service.clientcheck.ClientCheckService;
import ua.com.alevel.util.Util;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class OrderMapper {
    private OrderMapper() {
    }

    public static ClientCheck createOrderParamsToClientCheck(ClientCheck clientCheck, CreateOrderParams createOrderParams) {
        clientCheck.setDeliveryType(createOrderParams.getDeliveryType());
        clientCheck.setDeliveryAddress(createOrderParams.getDeliveryAddress());
        clientCheck.setPaymentType(createOrderParams.getPaymentType());
        clientCheck.setRecipient(createOrderParams.getRecipient());
        clientCheck.setRecipientNumberPhone(createOrderParams.getRecipientNumberPhone());

        return clientCheck;
    }

    public static ClientCheck fillClientCheckDefaultValues(ClientCheck clientCheck) {
        String defaultValue = "default";

        clientCheck.setDeliveryType(defaultValue);
        clientCheck.setDeliveryAddress(defaultValue);
        clientCheck.setPaymentType(defaultValue);
        clientCheck.setRecipient(defaultValue);
        clientCheck.setRecipientNumberPhone(defaultValue);

        return clientCheck;
    }

    public static TablesForFirstStatistic getListTablesForEighthStatistic(SalesSettingsForSpecificModels salesSettingsForSpecificModels, ClientCheckService clientCheckService) throws Exception {
        List<SalesSettingsForSpecificModelsParams> salesSettingsForSpecificModelsParamsList = new ArrayList<>();

        for (int year = LocalDate.now().getYear(); year != LocalDate.now().getYear() - 3; year--) {
            salesSettingsForSpecificModels.setYear(year + "");

            if (year == LocalDate.now().getYear()) {
                SalesSettingsForSpecificModelsParams salesSettingsForSpecificModelsParams = clientCheckService.getStoreEarningsByMonthAndYear(salesSettingsForSpecificModels);

                int monthNumber = salesSettingsForSpecificModelsParams.getFields().size() + 1;

                for (; monthNumber < 13; monthNumber++) {
                    salesSettingsForSpecificModelsParams.getFields().add(new SalesSettingsForSpecificModelsMonths(Util.getMonth(monthNumber), -1));
                }

                salesSettingsForSpecificModelsParamsList.add(salesSettingsForSpecificModelsParams);
            }
            else {
                salesSettingsForSpecificModelsParamsList.add(clientCheckService.getStoreEarningsByMonthAndYear(salesSettingsForSpecificModels));
            }
        }

        TablesForFirstStatistic tablesForFirstStatistic = new TablesForFirstStatistic();

        tablesForFirstStatistic.setTempYear(salesSettingsForSpecificModelsParamsList.get(0).getFields());
        tablesForFirstStatistic.setLastYear(salesSettingsForSpecificModelsParamsList.get(1).getFields());
        tablesForFirstStatistic.setYearBeforeLast(salesSettingsForSpecificModelsParamsList.get(2).getFields());

        return tablesForFirstStatistic;
    }
}