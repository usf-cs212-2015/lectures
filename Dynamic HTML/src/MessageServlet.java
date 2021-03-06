import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

// More XSS Prevention:
// https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet

// Apache Comments:
// http://commons.apache.org/proper/commons-lang/download_lang.cgi

@SuppressWarnings("serial")
public class MessageServlet extends HttpServlet {
	private static final String TITLE = "Messages";
	private static Logger log = Log.getRootLogger();

	private LinkedList<String> messages;

	public MessageServlet() {
		super();
		messages = new LinkedList<>();
	}

	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
        out.printf("<head><title>%s</title></head>%n", TITLE);
		out.printf("<body>%n");

		out.printf("<h1>Message Board</h1>%n%n");

		synchronized (messages) {
			for (String message : messages) {
				out.printf("<p>%s</p>%n%n", message);
			}
		}

		printForm(request, response);

		out.printf("<p>This request was handled by thread %s.</p>%n",
				Thread.currentThread().getName());

		out.printf("%n</body>%n");
		out.printf("</html>%n");

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		String username = request.getParameter("username");
		String message = request.getParameter("message");

		username = username == null ? "anonymous" : username;
		message = message == null ? "" : message;

		// Avoid XSS attacks using Apache Commons StringUtils
		// Comment out if you don't have this library installed
		username = StringEscapeUtils.escapeHtml4(username);
		message = StringEscapeUtils.escapeHtml4(message);

		String formatted = String.format(
				"%s<br><font size=\"-2\">[ posted by %s at %s ]</font>",
				message, username, getDate());

		synchronized (messages) {
			messages.addLast(formatted);

			while (messages.size() > 2) {
			    String first = messages.removeFirst();
				log.info("Removing message: " + first);
			}
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	private static void printForm(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());
		out.printf("<table cellspacing=\"0\" cellpadding=\"2\"%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>Name:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"username\" maxlength=\"50\" size=\"20\">%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>Message:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"message\" maxlength=\"100\" size=\"60\">%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("</table>%n");
		out.printf("<p><input type=\"submit\" value=\"Post Message\"></p>\n%n");
		out.printf("</form>\n%n");
	}

	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}