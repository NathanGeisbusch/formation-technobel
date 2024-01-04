package be.technobel.employees.db;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.math.BigDecimal;

public class BigDecimalAverageCollector implements Collector<BigDecimal, BigDecimalAverageCollector.BigDecimalAccumulator, BigDecimal> {
    public static class BigDecimalAccumulator {
        private BigDecimal sum = BigDecimal.ZERO;
        private BigDecimal count = BigDecimal.ZERO;
        public BigDecimal result() {
            return count.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : sum.divide(count, RoundingMode.FLOOR);
        }
        public void add(BigDecimal value) {
            sum = sum.add(value);
            count = count.add(BigDecimal.ONE);
        }
        public BigDecimalAccumulator combine(BigDecimalAccumulator another) {
            this.sum = this.sum.add(another.sum);
            return this;
        }
    }

    @Override
    public Supplier<BigDecimalAccumulator> supplier() {
        return BigDecimalAccumulator::new;
    }

    @Override
    public BiConsumer<BigDecimalAccumulator, BigDecimal> accumulator() {
        return BigDecimalAccumulator::add;
    }

    @Override
    public BinaryOperator<BigDecimalAccumulator> combiner() {
        return BigDecimalAccumulator::combine;
    }

    @Override
    public Function<BigDecimalAccumulator, BigDecimal> finisher() {
        return BigDecimalAccumulator::result;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    public static BigDecimalAverageCollector avgBigDecimal() {
        return new BigDecimalAverageCollector();
    }
}