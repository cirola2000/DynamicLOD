package dataid.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dataid.lov.UpdateLOVFilter;

public class UpdateLOV  extends HttpServlet{
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		UpdateLOVFilter a = new UpdateLOVFilter();
		response.getWriter().print(a.status);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		UpdateLOVFilter a = new UpdateLOVFilter();
		response.getWriter().print(a.status);
	}
}
