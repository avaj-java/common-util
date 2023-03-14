package jaemisseo.man.util

import java.sql.Clob
import java.sql.SQLException

class ClobUtil {

    public static String convertToString(Clob clob) throws SQLException, IOException {
        String result = ""
        if (clob == null)
            return result;

        final StringBuilder sb = new StringBuilder();

        try {
            final Reader reader = clob.getCharacterStream();
            final BufferedReader br = new BufferedReader(reader);

            int b;
            while (-1 != (b = br.read())){
                sb.append((char)b);
            }

            br.close();

        }catch (SQLException sqle){
            throw sqle
        }catch (IOException ioe){
            throw ioe
        }

        result = sb.toString();

        return result
    }

}
