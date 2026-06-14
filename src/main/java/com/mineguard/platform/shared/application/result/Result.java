package com.mineguard.platform.shared.application.result;

import java.util.Optional;
import java.util.function.Function;

/**
 * Represents the result of a command or operation that can either succeed with a value or fail with an error.
 * This type encodes the possibility of failure in the type system, making error cases explicit and composable.
 *
 * @param <T> The type of the successful result value
 * @param <E> The type of the error/failure information
 */
public sealed interface Result<T, E> {

    /** Represents a successful result containing a value. */
    record Success<T, E>(T value) implements Result<T, E> {
    }

    /** Represents a failed result containing error information. */
    record Failure<T, E>(E error) implements Result<T, E> {
    }

    /** Creates a successful result with the given value. */
    static <T, E> Result<T, E> success(T value) {
        return new Success<>(value);
    }

    /** Creates a failed result with the given error. */
    static <T, E> Result<T, E> failure(E error) {
        return new Failure<>(error);
    }

    /** Returns true if this result is a success. */
    default boolean isSuccess() {
        return this instanceof Success;
    }

    /** Returns true if this result is a failure. */
    default boolean isFailure() {
        return this instanceof Failure;
    }

    /** Returns an Optional containing the value if this is a success, otherwise empty. */
    default Optional<T> toOptional() {
        return switch (this) {
            case Success<T, E> s -> Optional.of(s.value);
            case Failure<T, E> f -> Optional.empty();
        };
    }

    /** Extracts the value if successful, or returns the given default if failed. */
    default T getOrElse(T defaultValue) {
        return switch (this) {
            case Success<T, E> s -> s.value;
            case Failure<T, E> f -> defaultValue;
        };
    }

    /** Applies a function to the error if this is a failure. */
    default <E2> Result<T, E2> mapError(Function<E, E2> f) {
        return switch (this) {
            case Success<T, E> s -> Result.success(s.value);
            case Failure<T, E> failure -> Result.failure(f.apply(failure.error));
        };
    }

    /** Applies a function to the value if this is a success, producing a new Result. */
    default <T2> Result<T2, E> flatMap(Function<T, Result<T2, E>> f) {
        return switch (this) {
            case Success<T, E> s -> f.apply(s.value);
            case Failure<T, E> failure -> Result.failure(failure.error);
        };
    }

    /** Applies a function to the value if this is a success. */
    default <T2> Result<T2, E> map(Function<T, T2> f) {
        return switch (this) {
            case Success<T, E> s -> Result.success(f.apply(s.value));
            case Failure<T, E> failure -> Result.failure(failure.error);
        };
    }

    /** Applies a fallback recovery function to the error if this is a failure. */
    default Result<T, E> recover(Function<E, Result<T, E>> f) {
        return switch (this) {
            case Success<T, E> s -> this;
            case Failure<T, E> failure -> f.apply(failure.error);
        };
    }
}
