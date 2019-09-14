package com.dzenm.helper.os;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dzenm
 * @date 2019-07-31 23:28
 */
@StringDef({Rom.MIUI, Rom.EMUI, Rom.FLYME, Rom.OPPO, Rom.SMARTISAN, Rom.VIVO, Rom.QIKU, Rom.ANDROID})
@Retention(RetentionPolicy.SOURCE)
public @interface Rom {
    String MIUI = "MIUI";
    String EMUI = "EMUI";
    String FLYME = "FLYME";
    String OPPO = "OPPO";
    String SMARTISAN = "SMARTISAN";
    String VIVO = "VIVO";
    String QIKU = "QIKU";
    String ANDROID = "ANDROID";
}
