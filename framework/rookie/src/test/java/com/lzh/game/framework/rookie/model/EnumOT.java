package com.lzh.game.framework.rookie.model;

import lombok.Data;

/**
 * @author zehong.l
 * @since 2024-09-20 16:15
 **/
@Data
public class EnumOT {

    private OT ot;

    private TestEnum enumOT;

    private TestEnum[] enumArray;

    public static EnumOT of() {
        var ot = new EnumOT();
        ot.ot = OT.createOT();
        ot.enumArray = new TestEnum[]{TestEnum.SSS,TestEnum.SS};
        ot.enumOT = TestEnum.SSS;
        return ot;
    }
}
