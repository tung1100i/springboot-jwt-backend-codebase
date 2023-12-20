package com.sapo.mock.techshop.common.constant;

public class HttpStatusConstant {
    private HttpStatusConstant() {
    }

    // success
    public static final  String CREATE_SUCCESS_MESSAGE = "CREATE SUCCESS";
    public static final  String SUCCESS_MESSAGE = "SUCCESS";

    // unknown error
    public static final String PROPERTY_NOT_IN_DATABASE = "Property of the given name is not present in the database.";
    public static final String INVALID_USER_ID = "The user_id field is required";

    // sql exception
    public static final String SQL_ERROR_CODE = "SQL ERROR";
    public static final String TYPE_NOT_DEFINE = "Type not defined";
    public static final String OUT_RANGE_SIZE = "The request size exceeds the limit of 50000 records";
    public static final String BULK_INSERT = "Successfully imported %s records. Failed imported records might occur due to the item_id field is not present, or item_id exceeds the max length of 128 characters";
    public static final String INVALID_PROPERTY_ITEM = "Property name does not match ^[a-zA-Z_][0-9a-zA-Z_]*$, or it is a reserved keyword (‘’id’’, ‘’item_id’’), or its length exceeds 50 characters. Type information is missing, or the given type is invalid.";
    public static final String INVALID_PROPERTY_USER = "Property name does not match ^[a-zA-Z_][0-9a-zA-Z_]*$, or it is a reserved keyword (‘’id’’, ‘’user_id’’), or its length exceeds 50 characters. Type information is missing, or the given type is invalid.";
    public static final String CONFLICT_PROPERTY_ITEM = "Property of the given name is already present in the database. In many cases, you may consider this code success – it only tells you that nothing has been written to the database.";
    public static final String CONFLICT_PROPERTY_USER = "Property of the given name is already present in the database.";
    public static final String ITEM_PROPERTY_NOT_EXIST = "Property of the given name is not present in the database. In many cases, you may consider this code success – it only tells you that nothing has been deleted from the database since the item property was already not present."
    ;


}