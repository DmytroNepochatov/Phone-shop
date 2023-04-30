package ua.com.webservice.util;

import ua.com.webservice.model.check.ClientCheck;
import ua.com.webservice.model.dto.OrdersForSelectUserForAdmin;
import ua.com.webservice.model.dto.PhoneColors;
import ua.com.webservice.model.dto.PhoneForAddToCart;
import ua.com.webservice.model.dto.filterparams.FilterSettings;
import ua.com.webservice.model.user.RegisteredUser;
import ua.com.webservice.service.phone.PhoneInstanceService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import static org.apache.commons.lang.NumberUtils.isNumber;

public final class Util {
    private static final String[] MONTHS = new String[]{"Січ", "Лют", "Бер", "Кві", "Тра", "Чер", "Лип", "Сер", "Вер", "Жов", "Лис", "Груд"};

    private Util() {
    }

    public static List<String> getYearsForListRegisteredUsers(List<RegisteredUser> registeredUsers, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.ENGLISH);
        List<String> years = new ArrayList<>();
        registeredUsers.forEach(user -> years.add(formatter.format(user.getDateOfBirth())));

        return years;
    }

    public static OrdersForSelectUserForAdmin createOrderForSelectUserForAdmin(ClientCheck check, SimpleDateFormat formatter, PhoneInstanceService phoneInstanceService) {
        OrdersForSelectUserForAdmin order = new OrdersForSelectUserForAdmin();
        order.setCheck(check);
        order.setTotalPrices(phoneInstanceService.findPriceForClientCheckId(check.getId()));
        order.setDates(formatter.format(check.getCreated()));

        if (check.getClosedDate() != null) {
            order.setDatesClosed(formatter.format(check.getClosedDate()));
            order.setFlag(true);
        }
        else {
            order.setFlag(false);
        }

        return order;
    }

    public static List<String> getListYears() {
        List<String> years = new ArrayList<>();
        int year = LocalDate.now().getYear();

        while (LocalDate.now().getYear() - 3 != year) {
            years.add("" + year);
            year = year - 1;
        }

        return years;
    }

    public static Date getDateMinusTwoMonth(Date date, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, amount);

        return cal.getTime();
    }

    public static String getAgeFromDate(String date) {
        String[] strBirth = date.split("\\.");

        LocalDate dateBirth = LocalDate.of(Integer.parseInt(strBirth[2]),
                Integer.parseInt(strBirth[1]), Integer.parseInt(strBirth[0]));
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dateBirth, currentDate);

        return "" + period.getYears();
    }

    public static String getMonth(int month) {
        return MONTHS[month - 1];
    }

    public static int checkColorsCount(PhoneForAddToCart phoneForAddToCart) {
        int checks = 0;

        for (PhoneColors phoneColor : phoneForAddToCart.getPhoneColors()) {
            if (phoneColor.isEnabled()) {
                checks++;
            }
        }

        return checks;
    }

    public static String findColor(PhoneForAddToCart phoneForAddToCart) {
        for (PhoneColors phoneColor : phoneForAddToCart.getPhoneColors()) {
            if (phoneColor.isEnabled()) {
                return phoneColor.getColor();
            }
        }

        return null;
    }

    public static boolean isValidDate(String dateStr, String datePattern) {
        DateFormat sdf = new SimpleDateFormat(datePattern);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        }
        catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isRequiredAge(String dateStr, int requiredAge) {
        String[] strBirth = dateStr.split("\\.");

        LocalDate dateBirth = LocalDate.of(Integer.parseInt(strBirth[2]),
                Integer.parseInt(strBirth[1]), Integer.parseInt(strBirth[0]));
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dateBirth, currentDate);

        return period.getYears() >= requiredAge;
    }

    public static FilterSettings filterSettingsFilling(FilterSettings filterSettings, String[] params) {
        for (int i = 0; i < params.length; i++) {
            String[] fields = params[i].split("=");
            filterSettings.getBrandForMainViewList().forEach(element -> {
                if (element.getId().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getChargeTypeForMainViewList().forEach(element -> {
                if (element.getId().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getCommunicationStandardForMainViewList().forEach(element -> {
                if (element.getId().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getOperationSystemForMainViewList().forEach(element -> {
                if (element.getId().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getProcessorForMainViewList().forEach(element -> {
                if (element.getId().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getTypeScreenForMainViewList().forEach(element -> {
                if (element.getId().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getDisplayResolutionForMainViewList().forEach(element -> {
                if (element.getDisplayResolution().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getDegreeOfMoistureProtectionForMainViewList().forEach(element -> {
                if (element.getDegreeOfMoistureProtection().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getNfcForMainViewList().forEach(element -> {
                if (element.getIsHaveNfc().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            if (isNumber(fields[1])) {

                filterSettings.getDiagonalForMainViewList().forEach(element -> {
                    if (element.getDiagonal() == Float.parseFloat(fields[1])) {
                        element.setEnabled(true);
                    }
                });


                filterSettings.getScreenRefreshRateForMainViewList().forEach(element -> {
                    if (element.getRefreshRate() == Integer.parseInt(fields[1])) {
                        element.setEnabled(true);
                    }
                });

                filterSettings.getNumberOfSimCardForMainViewList().forEach(element -> {
                    if (element.getNumberOfSimCards() == Integer.parseInt(fields[1])) {
                        element.setEnabled(true);
                    }
                });

                filterSettings.getAmountOfBuiltInMemoryForMainViewList().forEach(element -> {
                    if (element.getAmountOfBuiltInMemory() == Integer.parseInt(fields[1])) {
                        element.setEnabled(true);
                    }
                });

                filterSettings.getAmountOfRamForMainViewList().forEach(element -> {
                    if (element.getAmountOfRam() == Integer.parseInt(fields[1])) {
                        element.setEnabled(true);
                    }
                });

                filterSettings.getNumberOfFrontCameraForMainViewList().forEach(element -> {
                    if (element.getNumberOfFrontCameras() == Integer.parseInt(fields[1])) {
                        element.setEnabled(true);
                    }
                });

                filterSettings.getNumberOfMainCameraForMainViewList().forEach(element -> {
                    if (element.getNumberOfMainCameras() == Integer.parseInt(fields[1])) {
                        element.setEnabled(true);
                    }
                });
            }
        }

        return filterSettings;
    }

    public static List<String> filterParams(FilterSettings filterSettings, List<String> params) {
        filterSettings.getBrandForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.brand_id=" + element.getId());
            }
        });

        filterSettings.getChargeTypeForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.charge_type_id=" + element.getId());
            }
        });

        filterSettings.getCommunicationStandardForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.communication_standard_id=" + element.getId());
            }
        });

        filterSettings.getOperationSystemForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.operation_system_id=" + element.getId());
            }
        });

        filterSettings.getProcessorForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.processor_id=" + element.getId());
            }
        });

        filterSettings.getTypeScreenForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.type_screen_id=" + element.getId());
            }
        });

        filterSettings.getDiagonalForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.diagonal=" + element.getDiagonal());
            }
        });

        filterSettings.getDisplayResolutionForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.display_resolution=" + element.getDisplayResolution());
            }
        });

        filterSettings.getScreenRefreshRateForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.screen_refresh_rate=" + element.getRefreshRate());
            }
        });

        filterSettings.getNumberOfSimCardForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.number_of_sim_cards=" + element.getNumberOfSimCards());
            }
        });

        filterSettings.getAmountOfBuiltInMemoryForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.amount_of_built_in_memory=" + element.getAmountOfBuiltInMemory());
            }
        });

        filterSettings.getAmountOfRamForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.amount_of_ram=" + element.getAmountOfRam());
            }
        });

        filterSettings.getNumberOfFrontCameraForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.number_of_front_cameras=" + element.getNumberOfFrontCameras());
            }
        });

        filterSettings.getNumberOfMainCameraForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.number_of_main_cameras=" + element.getNumberOfMainCameras());
            }
        });

        filterSettings.getDegreeOfMoistureProtectionForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.degree_of_moisture_protection=" + element.getDegreeOfMoistureProtection());
            }
        });

        filterSettings.getNfcForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.is_have_nfc=" + element.getIsHaveNfc());
            }
        });

        return params;
    }

    public static String translate(String text) {
        String output = "";

        try {
            String jsonPayload = new StringBuilder()
                    .append("{")
                    .append("\"fromLang\":\"")
                    .append("en")
                    .append("\",")
                    .append("\"toLang\":\"")
                    .append("uk")
                    .append("\",")
                    .append("\"text\":\"")
                    .append(text)
                    .append("\"")
                    .append("}")
                    .toString();

            URL url = new URL("http://api.whatsmate.net/v1/translation/translate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-WM-CLIENT-ID", "FREE_TRIAL_ACCOUNT");
            conn.setRequestProperty("X-WM-CLIENT-SECRET", "PUBLIC_SECRET");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(jsonPayload.getBytes());
            os.flush();
            os.close();

            int statusCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (statusCode == 200) ? conn.getInputStream() : conn.getErrorStream()
            ));

            output = br.readLine();
            conn.disconnect();
        }
        catch (Exception e) {
        }

        return output;
    }
}
