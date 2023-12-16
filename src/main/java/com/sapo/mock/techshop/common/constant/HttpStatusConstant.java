package com.sapo.mock.techshop.common.constant;

public class HttpStatusConstant {
    // success
    public final static String CREATE_SUCCESS_MESSAGE = "CREATE SUCCESS";
    public final static String SUCCESS_MESSAGE = "SUCCESS";

    // unknown error
    public final static String PROPERTY_NOT_IN_DATABASE = "Property of the given name is not present in the database.";

    // sql exception
    public final static String SQL_ERROR_CODE = "SQL ERROR";
    public final static String TYPE_NOT_DEFINE = "Type not defined";
    public final static String OUT_RANGE_SIZE = "The request size exceeds the limit of 50000 records";
    public final static String BULK_INSERT = "Successfully imported %s records. Failed imported records might occur due to the item_id field is not present, or item_id exceeds the max length of 128 characters";
    public final static String INVALID_PROPERTY_ITEM = "Property name does not match ^[a-zA-Z_][0-9a-zA-Z_]*$, or it is a reserved keyword (‘’id’’, ‘’item_id’’), or its length exceeds 50 characters. Type information is missing, or the given type is invalid.";
    public final static String INVALID_PROPERTY_USER = "Property name does not match ^[a-zA-Z_][0-9a-zA-Z_]*$, or it is a reserved keyword (‘’id’’, ‘’user_id’’), or its length exceeds 50 characters. Type information is missing, or the given type is invalid.";
    public final static String CONFLICT_PROPERTY_ITEM = "Property of the given name is already present in the database. In many cases, you may consider this code success – it only tells you that nothing has been written to the database.";
    public final static String CONFLICT_PROPERTY_USER = "Property of the given name is already present in the database.";
    public final static String ITEM_PROPERTY_NOT_EXIST = "Property of the given name is not present in the database. In many cases, you may consider this code success – it only tells you that nothing has been deleted from the database since the item property was already not present."
    ;


}