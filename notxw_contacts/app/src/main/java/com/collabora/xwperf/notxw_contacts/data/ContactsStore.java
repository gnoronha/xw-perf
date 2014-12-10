package com.collabora.xwperf.notxw_contacts.data;


import android.provider.BaseColumns;

import com.annotatedsql.annotation.provider.Provider;
import com.annotatedsql.annotation.provider.URI;
import com.annotatedsql.annotation.sql.Autoincrement;
import com.annotatedsql.annotation.sql.Column;
import com.annotatedsql.annotation.sql.Column.Type;
import com.annotatedsql.annotation.sql.NotNull;
import com.annotatedsql.annotation.sql.PrimaryKey;
import com.annotatedsql.annotation.sql.Schema;
import com.annotatedsql.annotation.sql.Table;

@Schema(className = "ContactsSchema", dbName = "contacts.db", dbVersion = 2)
@Provider(authority = "com.collabora.xwperf.notxw_contacts.data", name = "ContactsContentProvider", schemaClass = "ContactsSchema")
public interface ContactsStore {
    @Table(ContactTable.TABLE_NAME)
    public static interface ContactTable {
        String TABLE_NAME = "contactsTable";
        @URI
        String CONTENT_URI = "contact";

        @PrimaryKey
        @Autoincrement
        @Column(type = Type.INTEGER)
        String ID = BaseColumns._ID;

        @NotNull
        @Column(type = Type.TEXT)
        String NAME = "forename";

        @Column(type = Type.TEXT)
        String EMAIL = "email";

        @Column(type = Type.INTEGER)
        String AVATAR = "avatar";

        @Column(type = Type.TEXT)
        String PHONE = "phone";

        @Column(type = Type.REAL)
        String BIRTHDAY = "birthday";

        @Column(type = Type.INTEGER)
        String FAVORITE = "favorite";
    }
}


