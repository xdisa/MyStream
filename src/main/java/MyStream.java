import java.util.Arrays;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class MyStream<T> {
    private final Consumer<Consumer<T>> action;

    public MyStream(Consumer<Consumer<T>> action) {
        this.action = action;
    }

    public void forEach(Consumer<T> cons) {
        action.accept(cons);
    }


    public static <T> MyStream<T> of(Iterable<T> elements) {
        return new MyStream<>(elements::forEach);
    }


    public static <T> MyStream<T> of(T... elements) {
        return of(Arrays.asList(elements));
    }


    public <U> MyStream<U> map(Function<T, U> mapper) {
        return new MyStream<>(cons -> forEach(e -> cons.accept(mapper.apply(e))));
    }

    public MyStream<T> filter(Predicate<T> pred) {
        return new MyStream<>(cons -> forEach(e -> {
            if(pred.test(e))
                cons.accept(e);
        }));
    }

    public T reduce(T identity, BinaryOperator<T> op) {
        class Box {
            T val = identity;
        }
        Box b = new Box();
        forEach(e -> b.val = op.apply(b.val, e));
        return b.val;
    }

    public Optional<T> reduce(BinaryOperator<T> op) {
        class Box {
            boolean isPresent;
            T val;
        }
        Box b = new Box();
        forEach(e -> {
            if(b.isPresent) b.val = op.apply(b.val, e);
            else {
                b.val = e;
                b.isPresent = true;
            }
        });
        return b.isPresent ? Optional.empty() : Optional.of(b.val);
    }
    public long count() {
        return map(e -> 1L).reduce(0L, Long::sum);
    }









}