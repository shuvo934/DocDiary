package ttit.com.shuvo.docdiary.utilities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Exact decimal ordering; each source voucher row occurs once. */
public final class NativeAccountsNumbers {
    private NativeAccountsNumbers() { }
    public static boolean isBlank(String raw) {
        return raw == null || raw.trim().isEmpty() || "null".equalsIgnoreCase(raw.trim());
    }
    public static BigDecimal decimal(String raw) {
        return isBlank(raw) ? BigDecimal.ZERO : new BigDecimal(raw.trim());
    }
    public static String label(String raw) {
        return decimal(raw).stripTrailingZeros().toPlainString();
    }
    public interface AmountAccessor<T> { String amount(T row); }
    public static <T> ArrayList<T> sortDebitThenCredit(List<T> rows, AmountAccessor<T> debit, AmountAccessor<T> credit) {
        ArrayList<T> debits = new ArrayList<>(), credits = new ArrayList<>();
        for (T row : rows) {
            String dr = debit.amount(row), cr = credit.amount(row);
            if (isBlank(cr) && !isBlank(dr)) { decimal(dr); debits.add(row); }
            else if (isBlank(dr) && !isBlank(cr)) { decimal(cr); credits.add(row); }
        }
        Collections.sort(debits, (left, right) -> decimal(debit.amount(right)).compareTo(decimal(debit.amount(left))));
        Collections.sort(credits, (left, right) -> decimal(credit.amount(right)).compareTo(decimal(credit.amount(left))));
        ArrayList<T> result = new ArrayList<>(debits.size() + credits.size());
        result.addAll(debits); result.addAll(credits); return result;
    }
}
