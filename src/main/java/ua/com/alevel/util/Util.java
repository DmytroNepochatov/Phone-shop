package ua.com.alevel.util;

import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.dto.OrdersForSelectUserForAdmin;
import ua.com.alevel.model.dto.PhoneColors;
import ua.com.alevel.model.dto.PhoneForAddToCart;
import ua.com.alevel.model.dto.filterparams.FilterSettings;
import ua.com.alevel.model.user.RegisteredUser;
import ua.com.alevel.service.phone.PhoneInstanceService;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static org.apache.commons.lang.NumberUtils.isNumber;

public final class Util {
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

    public static String getAgeFromDate(String date) {
        String[] strBirth = date.split("\\.");

        LocalDate dateBirth = LocalDate.of(Integer.parseInt(strBirth[2]),
                Integer.parseInt(strBirth[1]), Integer.parseInt(strBirth[0]));
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dateBirth, currentDate);

        return "" + period.getYears();
    }

    public static String getMonth(int month) {
        return new DateFormatSymbols(Locale.ENGLISH).getMonths()[month - 1];
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
}
