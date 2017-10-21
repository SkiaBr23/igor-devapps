package br.unb.igor.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import br.unb.igor.model.Sessao;

public class Utils {

    public static SimpleDateFormat DateFormatter_MMddyy = new SimpleDateFormat("dd/MM/yy");

    public static Comparator<Sessao> ComparatorSessionByDateDesc =
        new Comparator<Sessao>() {
            @Override
            public int compare(Sessao l, Sessao r) {
                try {
                    Date left = DateFormatter_MMddyy.parse(l.getData());
                    Date right = DateFormatter_MMddyy.parse(r.getData());
                    return left.before(right) ? 1 : -1;
                } catch (ParseException e) {
                }
                return 0;
            }
        };

    public static Comparator<Sessao> ComparatorSessionByDateAsc =
        new Comparator<Sessao>() {
            @Override
            public int compare(Sessao l, Sessao r) {
                try {
                    Date left = DateFormatter_MMddyy.parse(l.getData());
                    Date right = DateFormatter_MMddyy.parse(r.getData());
                    return left.after(right) || left.equals(right) ? 1 : -1;
                } catch (ParseException e) {
                }
                return 0;
            }
        };
}
