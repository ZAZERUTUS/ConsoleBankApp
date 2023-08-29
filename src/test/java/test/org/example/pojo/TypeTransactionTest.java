package test.org.example.pojo;

import org.example.pojo.TypeTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TypeTransactionTest {

    @ParameterizedTest(name = "{index} -  {0} success converted in {1}")
    @CsvSource(value = {
            "ПоПолНение, CASH_IN, Пополнение",
            "СПИСАНИЕ, CASH_OUT, Списание",
            "cAsH_ouT, CASH_OUT, Списание"
    })
    public void testConvertType(String in, TypeTransaction typeTransaction, String out) {
        TypeTransaction typeConverted = TypeTransaction.getTypeByValue(in);
        Assertions.assertEquals(typeTransaction, typeConverted);
        Assertions.assertEquals(typeTransaction.getType(), out);
    }

    @Test
    public void testIllegalArgumentForConvert() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> TypeTransaction.getTypeByValue("wecwc"));
    }

}
