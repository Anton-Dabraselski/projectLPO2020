package projectLPO.environments;

import projectLPO.parser.ast.VarIdent;

public interface Environment<T> {

	/* adds a new nested scope */

	void enterScope();

	/* removes the most nested scope */

	void exitScope();

	/*
	 * lookups the value associated with id starting from the most nested scope;
	 * throws an EnvironmentException if id could not be found in any scope
	 */

	T lookup(VarIdent id);

	/*
	 * updates the most nested scope by associating id with payload; id is not allowed
	 * to be already defined, id and payload must be non-null
	 */

	T dec(VarIdent id, T payload);

	/*
	 * updates the most nested scope which defines id by associating id with
	 * payload; throws an EnvironmentException if id could not be found in any
	 * scope; id and payload must be non-null
	 */

	T update(VarIdent id, T payload);

}
