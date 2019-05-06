/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query.sqm.tree.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.criteria.Expression;

import org.hibernate.query.criteria.JpaSelection;
import org.hibernate.query.sqm.NodeBuilder;
import org.hibernate.query.sqm.produce.SqmTreeCreationLogger;
import org.hibernate.query.sqm.tree.jpa.AbstractJpaSelection;
import org.hibernate.query.sqm.tree.predicate.SqmPredicate;
import org.hibernate.sql.ast.produce.metamodel.spi.ExpressableType;
import org.hibernate.type.spi.StandardSpiBasicTypes;

import static org.hibernate.query.internal.QueryHelper.highestPrecedenceType;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractSqmExpression<T> extends AbstractJpaSelection<T> implements SqmExpression<T> {

	public AbstractSqmExpression(ExpressableType<T> type, NodeBuilder criteriaBuilder) {
		super( type, criteriaBuilder );
	}

	@Override
	public final void applyInferableType(ExpressableType<?> type) {
		if ( type == null ) {
			return;
		}

		final ExpressableType<?> oldType = getExpressableType();

		final ExpressableType<?> newType = highestPrecedenceType( oldType, type );
		if ( newType != null && newType != oldType ) {
			internalApplyInferableType( newType );
		}
	}

	@SuppressWarnings("unchecked")
	protected void internalApplyInferableType(ExpressableType<?> newType) {
		SqmTreeCreationLogger.LOGGER.debugf(
				"Applying inferable type to SqmExpression [%s] : %s -> %s",
				this,
				getExpressableType(),
				newType
		);
		setExpressableType( highestPrecedenceType( newType, getExpressableType() ) );
	}

	@Override
	public SqmExpression<Long> asLong() {
		return castAs( StandardSpiBasicTypes.LONG );
	}

	@Override
	public SqmExpression<Integer> asInteger() {
		return castAs( StandardSpiBasicTypes.INTEGER );
	}

	@Override
	public SqmExpression<Float> asFloat() {
		return castAs( StandardSpiBasicTypes.FLOAT );
	}

	@Override
	public SqmExpression<Double> asDouble() {
		return castAs( StandardSpiBasicTypes.DOUBLE );
	}

	@Override
	public SqmExpression<BigDecimal> asBigDecimal() {
		return castAs( StandardSpiBasicTypes.BIG_DECIMAL );
	}

	@Override
	public SqmExpression<BigInteger> asBigInteger() {
		return castAs( StandardSpiBasicTypes.BIG_INTEGER );
	}

	@Override
	public SqmExpression<String> asString() {
		return castAs( StandardSpiBasicTypes.STRING );
	}

	@Override
	public <X> SqmExpression<X> as(Class<X> type) {
		return nodeBuilder().cast(this, type);
	}

	@Override
	public SqmPredicate isNull() {
		return nodeBuilder().isNull( this );
	}

	@Override
	public SqmPredicate isNotNull() {
		return nodeBuilder().isNotNull( this );
	}

	@Override
	public SqmPredicate in(Object... values) {
		return nodeBuilder().in( this, values );
	}

	@Override
	public SqmPredicate in(Expression<?>... values) {
		return nodeBuilder().in( this, values );
	}

	@Override
	public SqmPredicate in(Collection<?> values) {
		return nodeBuilder().in( this, values );
	}

	@Override
	public SqmPredicate in(Expression<Collection<?>> values) {
		return nodeBuilder().in( this, values );
	}

	@Override
	public JpaSelection<T> alias(String name) {
		setAlias( name );
		return this;
	}
}
