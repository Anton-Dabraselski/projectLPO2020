package projectLPO.environments;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import projectLPO.parser.ast.VarIdent;

import static java.util.Objects.requireNonNull;

public class GenEnvironment<T> implements Environment<T> {

	private LinkedList<Map<VarIdent, T>> scopeChain = new LinkedList<>();

	/*
	 * enter a new nested scope; private method shared by enterScope() and the
	 * constructor GenEnvironment()
	 */
	private void addEmptyScope() {
		scopeChain.addFirst(new HashMap<>());
	}

	/* create an environment with just one empty scope */
	public GenEnvironment() {
		addEmptyScope();
	}

	@Override
	public void enterScope() {
		addEmptyScope();
	}

	@Override
	public void exitScope() {
		scopeChain.removeFirst();
	}

	/*
	 * looks up id starting from the innermost scope; throws EnvironmentException
	 * if id could not be found in any scope
	 */

	protected Map<VarIdent, T> resolve(VarIdent id) {
		for (Map<VarIdent, T> scope : scopeChain)
			if (scope.containsKey(id))
				return scope;
		throw new EnvironmentException("Undeclared variable " + id.getName());
	}

	@Override
	public T lookup(VarIdent id) {
		return resolve(id).get(id);
	}

	/*
	 * updates map to associate id with payload; id and payload must be non-null
	 */

	private static <T> T updateScope(Map<VarIdent, T> map, VarIdent id, T payload) {
		return map.put(requireNonNull(id), requireNonNull(payload));
	}

	/*
	 * updates the most nested scope by associating id with payload; id is not allowed
	 * to be already defined, id and payload must be non-null
	 */

	@Override
	public T dec(VarIdent id, T payload) {
		Map<VarIdent, T> scope = scopeChain.getFirst();
		if (scope.containsKey(id))
			throw new EnvironmentException("Variable " + id.getName() + " already declared");
		return updateScope(scope, id, payload);
	}

	/*
	 * updates the payload of the most enclosed id, throws an exception if no id can be
	 * found in the scope chain. Only used for the dynamic semantics
	 */

	@Override
	public T update(VarIdent id, T payload) {
		Map<VarIdent, T> scope = resolve(id);
		return updateScope(scope, id, payload);
	}

}
