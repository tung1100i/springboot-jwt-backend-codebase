package com.sapo.mock.techshop.common.constant;

public class HttpStatusConstant {
    // success
    public final static String SUCCESS_CODE = "00000";
    public final static String CREATE_SUCCESS_MESSAGE = "CREATE SUCCESS";
    public final static String SUCCESS_MESSAGE = "SUCCESS";

    // unknown error
    public final static String PROPERTY_NOT_IN_DATABASE = "Property of the given name is not present in the database.";

    // sql exception
    public final static String SQL_ERROR_CODE = "SQL ERROR";
    public final static String OUT_RANGE_SIZE = "The request size exceeds the limit of 50000 records";
    public final static String BULK_INSERT = "Successfully imported %s records. Failed imported records might occur due to the item_id field is not present, or item_id exceeds the max length of 128 characters";


}