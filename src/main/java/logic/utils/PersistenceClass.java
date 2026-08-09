package logic.utils;

import logic.utils.enums.PersistenceTypes;

public class PersistenceClass {
    private PersistenceClass(){}

    //inizializzata di default a JDBC
    private static PersistenceTypes persistenceType = PersistenceTypes.JDBC;

    public static void setPersistenceType(PersistenceTypes persType){
        persistenceType = persType;
    }

    public static PersistenceTypes getPersistenceType(){
        return persistenceType;
    }
}
