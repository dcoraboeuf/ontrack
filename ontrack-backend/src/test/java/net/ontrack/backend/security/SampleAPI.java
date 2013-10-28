package net.ontrack.backend.security;

public interface SampleAPI {

    void no_constraint();

	void project_call_missing_param(int project);

	void project_call_too_much(int project, int additional);

	void project_call_ok(int project, String name);

}
