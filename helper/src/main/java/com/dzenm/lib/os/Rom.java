package com.dzenm.lib.os;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

/**
 * @author dzenm
 * @date 2019-07-31 23:28
 */
@StringDef({Rom.NONE, Rom.MIUI, Rom.EMUI, Rom.FLYME, Rom.OPPO, Rom.SMARTISAN,
        Rom.VIVO, Rom.QIKU, Rom.THREE_NINE_ZERO, Rom.ANDROID})
@Retention(RetentionPolicy.SOURCE)
public @interface Rom {
    String NONE = "";
    String MIUI = "MIUI";
    String EMUI = "EMUI";
    String FLYME = "FLYME";
    String OPPO = "OPPO";
    String SMARTISAN = "SMARTISAN";
    String VIVO = "VIVO";
    String QIKU = "QIKU";
    String THREE_NINE_ZERO = "360";
    String ANDROID = "ANDROID";
}
