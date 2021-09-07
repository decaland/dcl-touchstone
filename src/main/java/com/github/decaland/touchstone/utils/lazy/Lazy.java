package com.github.decaland.touchstone.utils.lazy;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Lazy<T> {

    private final Supplier<T> supplier;
    private T instance;

    @Contract(pure = true)
    private Lazy(@NotNull Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Contract("_ -> new")
    public static <T> @NotNull Lazy<T> using(@NotNull Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    @Contract(value = "_ -> new", pure = true)
    public static <S> @NotNull LazyBuilder<S> from(@NotNull S source) {
        return new LazyBuilder<>(source);
    }

    public @NotNull T get() {
        if (instance == null) {
            try {
                instance = Objects.requireNonNull(supplier.get());
            } catch (NullPointerException exception) {
                throw new RuntimeException(
                        "Lazily generated object received a supplier function that yielded null value"
                );
            }
        }
        return instance;
    }

    public static final class LazyBuilder<S> {

        private final S source;

        @Contract(pure = true)
        private LazyBuilder(@NotNull S source) {
            this.source = source;
        }

        @Contract(value = "_ -> new", pure = true)
        public <T> @NotNull Lazy<T> using(@NotNull Function<S, T> mappingFunction) {
            return new Lazy<>(() -> mappingFunction.apply(source));
        }
    }

    public static final class LazyBiBuilder<F, S> {

        private final F firstSource;
        private final S secondSource;

        @Contract(pure = true)
        public LazyBiBuilder(@NotNull F firstSource, @NotNull S secondSource) {
            this.firstSource = firstSource;
            this.secondSource = secondSource;
        }

        @Contract(value = "_ -> new", pure = true)
        public <T> @NotNull Lazy<T> using(@NotNull BiFunction<F, S, T> mappingFunction) {
            return new Lazy<>(() -> mappingFunction.apply(firstSource, secondSource));
        }
    }
}
