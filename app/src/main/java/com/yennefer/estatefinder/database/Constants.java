package com.yennefer.estatefinder.database;

/**
 * Created by Yennefer on 14.06.2016.
 * Класс, содержащий константы для работы с БД.
 */
public class Constants {

    // Путь к БД приложения
    public static final String DB_PATH = "/data/data/com.yennefer.estatefinder/databases/";

    // Название БД
    public static final String DB_NAME = "EstateAds.sqlite";

    // Текст ошибок
    public static final String ERROR_COPYING_DATABASE = "Error copying database.";

    // Названия таблц
    public static final String ADVERTS_TABLE = "Adverts";
    public static final String ADVERT_PARAMETERS_TABLE = "Advert_Parameters";
    public static final String SEARCHES_ADVERTS_TABLE = "Searches_Adverts";
    public static final String SEARCHES_TABLE = "Searches";
    public static final String SEARCH_PARAMETERS_TABLE = "Search_Parameters";
    public static final String PARAMETER_NAMES_TABLE = "Parameter_names";
    public static final String PARAMETER_VALUES_TABLE = "Parameter_values";

    // Названия полей
    public static final String ID_FIELD = "_id";
    public static final String ADVERT_ID_FIELD = "advert_id";
    public static final String SEARCH_ID_FIELD = "search_id";
    public static final String CATEGORY_ID_FIELD = "category_id";
    public static final String REGION_ID_FIELD = "region_id";
    public static final String PARAMETER_NAME_ID_FIELD = "parameter_name_id";
    public static final String PARAMETER_VALUE_ID_FIELD = "parameter_value_id";
    public static final String PARAMETER_NAME_FIELD = "parameter_name";
    public static final String PARAMETER_VALUE_FIELD = "parameter_value";
    public static final String NAME_FIELD = "name";
    public static final String API_ID_FIELD = "api_id";
    public static final String AVITO_ID_FIELD = "avito_id";
    public static final String URL_FIELD = "url";
    public static final String TITLE_FIELD = "title";
    public static final String PRICE_FIELD = "price";
    public static final String DATE_FIELD = "date";
    public static final String CITY_ID_FIELD = "city_id";
    public static final String CITY_FIELD = "city";
    public static final String DISTRICT_FIELD = "district";
    public static final String ADDRESS_FIELD = "address";
    public static final String METRO_FIELD = "metro";
    public static final String OPERATOR_FIELD = "operator";
    public static final String PHONE_FIELD = "phone";
    public static final String IMAGES_FIELD = "images";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String LAT_FIELD = "lat";
    public static final String LNG_FIELD = "lng";
    public static final String WITH_PHOTO_FIELD = "with_photo";
    public static final String START_DATE_FIELD = "start_date";
    public static final String END_DATE_FIELD = "end_date";
    public static final String PRICE_FROM_FIELD = "price_from";
    public static final String PRICE_TO_FIELD = "price_to";

    // Идентификаторы параметров
    public static final int ROOM_COUNT_ID = 2;
    public static final int HOME_TYPE_ID = 3;
    public static final int OBJECT_TYPE_ID = 4;
    public static final int LEASE_ID = 5;
    public static final int LOCATION_ID = 6;

    // Тексты запросов
    public static final String GET_SEARCHES_QUERY =
            "SELECT s._id, s.name, COUNT(s_a._id) AS advert_count FROM Searches s " +
            "LEFT JOIN Searches_Adverts s_a ON s._id = s_a.search_id GROUP BY s.name ORDER BY s._id";
    public static final String GET_RESULTS_QUERY =
            "SELECT  a._id, a.images, a.title, (printf(\"%.0f\", a.price) || \" руб.\") AS price, a.district, " +
            "(CASE WHEN a.date > datetime('now', 'start of day') THEN \"Сегодня\" " +
            "WHEN a.date < datetime('now', 'start of day') AND " +
            "a.date >= datetime('now', '-1 day', 'start of day') THEN \"Вчера\" ELSE strftime('%d.%m.%Y', a.date) END) " +
            "AS date, strftime('%H:%M', a.date) AS time FROM Adverts a JOIN Searches_Adverts s_a ON " +
            "a._id = s_a.advert_id WHERE s_a.search_id = ? ORDER BY a.date DESC";
    public static final String GET_ADVERT_QUERY =
            "SELECT images, title, (printf(\"%.0f\", price) || \" руб.\") AS price, address, lat, " +
            " lng, description, phone, name, url, city FROM Adverts WHERE _id = ?";
    public static final String GET_ADVERT_PARAMETERS_QUERY =
            "SELECT p_v.name AS parameter_value, p_n.name AS parameter_name FROM Advert_Parameters a_p " +
            "JOIN Parameter_values p_v ON a_p.parameter_value_id = p_v._id JOIN Parameter_names p_n " +
            "ON p_v.parameter_name_id = p_n._id WHERE a_p.advert_id = ?";
    public static final String GET_CATEGORIES_QUERY =
            "SELECT _id, name FROM Categories ORDER BY _id";
    public static final String GET_AD_TYPES_QUERY =
            "SELECT _id, name FROM Parameter_values WHERE parameter_name_id = 1 ORDER BY _id";
    public static final String GET_REGIONS_QUERY =
            "SELECT _id, name FROM Regions ORDER BY _id";
    public static final String GET_CITIES_BY_REGION =
            "SELECT _id, name FROM Cities WHERE region_id = ? ORDER BY _id";
    public static final String GET_PARAMETERS_QUERY =
            "SELECT _id, name FROM Parameter_values WHERE parameter_name_id = ? ORDER BY _id";
    public static final String DELETE_FROM_ADVERT_PARAMETERS_BY_SEARCH_ID_QUERY =
            "DELETE FROM Advert_Parameters WHERE advert_id IN (SELECT advert_id FROM Searches_Adverts WHERE search_id = ?)";
    public static final String DELETE_FROM_ADVERTS_BY_SEARCH_ID_QUERY =
            "DELETE FROM Adverts WHERE _id IN (SELECT advert_id FROM Searches_Adverts WHERE search_id = ?)";
    public static final String GET_SEARCH_TO_DO_QUERY =
            "SELECT _id FROM Searches WHERE date < current_timestamp";
    public static final String GET_CATEGORY_ID_BY_SEARCH_ID_QUERY =
            "SELECT c.api_id FROM Searches s JOIN Categories c ON s.category_id = c._id WHERE s._id = ?";
    public static final String GET_REGION_ID_BY_SEARCH_ID_QUERY =
            "SELECT r.api_id FROM Searches s JOIN Regions r ON s.region_id = r._id WHERE s._id = ?";
    public static final String GET_CITY_ID_BY_SEARCH_ID_QUERY =
            "SELECT city_id FROM Searches WHERE _id = ?";
    public static final String GET_CITY_BY_ID_QUERY =
            "SELECT name FROM Cities WHERE _id = ?";
    public static final String GET_WITH_PHOTO_BY_SEARCH_ID_QUERY =
            "SELECT with_photo FROM Searches WHERE _id = ?";
    public static final String GET_PRICE_FROM_BY_SEARCH_ID_QUERY =
            "SELECT price_from FROM Searches WHERE _id = ?";
    public static final String GET_PRICE_TO_BY_SEARCH_ID_QUERY =
            "SELECT price_to FROM Searches WHERE _id = ?";
    public static final String GET_DATE_BY_SEARCH_ID_QUERY =
            "SELECT DATETIME(date) AS end_date FROM Searches WHERE _id= ?";
    public static final String GET_DATE_MINUS_DAY_BY_SEARCH_ID_QUERY =
            "SELECT DATETIME(date, '-1 day') AS start_date FROM Searches WHERE _id = ?";
    public static final String GET_PARAMS_BY_SEARCH_ID_QUERY = "SELECT p_v.name AS parameter_value, " +
            "p_n.name AS parameter_name FROM Searches s "+
            "JOIN Search_Parameters s_p ON s._id = s_p.search_id "+
            "JOIN Parameter_values p_v ON s_p.parameter_value_id = p_v._id "+
            "JOIN Parameter_names p_n ON p_v.parameter_name_id = p_n._id "+
            "WHERE s._id = ?";
    public static final String GET_ADVERT_BY_AVITO_ID_AND_SEARCH_ID_QUERY =
            "SELECT a._id FROM Adverts a JOIN Searches_Adverts s_a ON a._id = s_a.advert_id " +
            "WHERE s_a.search_id = ? AND a.api_id = ? AND a.avito_id = ?";
    public static final String UPDATE_SEARCH_DATE_QUERY =
            "UPDATE Searches SET date = DATETIME(CURRENT_TIMESTAMP, '+1 day') WHERE _id = ?";
    public static final String GET_PARAMETER_ID_BY_NAME_QUERY =
            "SELECT _id FROM Parameter_names WHERE name = ?";
    public static final String GET_PARAMETER_VALUE_ID_BY_PARAMETER_NAME_QUERY =
            "SELECT _id FROM Parameter_values WHERE parameter_name_id = ? AND name = ?";
}
