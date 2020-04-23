package com.ha.net.eautoopen.dto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * MG日志集合
 */
@Data
@Document(collection = "gatewaylog")
@CompoundIndexes(
        {
                @CompoundIndex(name = "pro_indx",def = "{'PROCESS_ID':1}"),
                @CompoundIndex(name = "pro_cre_indx",def = "{'CREATE_DATE':-1,'PROCESS_ID':1}")
        })
public class MGLog implements Serializable {
    @Id
    private String id;

    @Field("PROCESS_ID")
    private String processId;

    @Field("CONSUMER")
    private String consumer;

    @Field("REQ_OUT")
    private String reqOut;

    @Field("RES_OUT")
    private String resOut;

    @Field("ERROR")
    private String error;

    @Field("CREATE_DATE")
    private Date createDate;

    @Field("UPDATE_DATE")
    private Date updateDate;

    @Field("EXPIRE_TIME")
    private Date expireTime;
}
