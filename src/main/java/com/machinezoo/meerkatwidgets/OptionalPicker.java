// Part of Meerkat Widgets: https://meerkatwidgets.machinezoo.com
package com.machinezoo.meerkatwidgets;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import com.machinezoo.pmsite.*;
import com.machinezoo.pushmode.dom.*;
import com.machinezoo.remorabindings.*;
import com.machinezoo.stagean.*;

/*
 * Variation of ItemPicker that allows empty Optional result.
 */
@DraftApi
public class OptionalPicker<T> {
    private String title;
    public OptionalPicker<T> title(String title) {
        this.title = title;
        return this;
    }
    private final List<T> items = new ArrayList<>();
    public OptionalPicker<T> add(T item) {
        Objects.requireNonNull(item, "Items must be non-null.");
        items.add(item);
        return this;
    }
    public OptionalPicker<T> add(Stream<? extends T> items) {
        items.forEach(this::add);
        return this;
    }
    public OptionalPicker<T> add(Collection<? extends T> items) { return add(items.stream()); }
    @SafeVarargs public final OptionalPicker<T> add(T... items) { return add(Arrays.stream(items)); }
    public OptionalPicker() {}
    public OptionalPicker(String title) { this.title = title; }
    @SafeVarargs public OptionalPicker(String title, T... items) {
        this.title = title;
        add(items);
    }
    public OptionalPicker(String title, Collection<? extends T> items) {
        this.title = title;
        add(items);
    }
    public OptionalPicker(String title, Stream<? extends T> items) {
        this.title = title;
        add(items);
    }
    private OptionalBinding<T> binding;
    public OptionalPicker<T> binding(OptionalBinding<T> binding) {
        this.binding = binding;
        return this;
    }
    private Function<T, String> naming = Objects::toString;
    public OptionalPicker<T> naming(Function<T, String> naming) {
        Objects.requireNonNull(naming);
        this.naming = naming;
        return this;
    }
    private boolean sidebar = true;
    public OptionalPicker<T> sidebar(boolean sidebar) {
        this.sidebar = sidebar;
        return this;
    }
    public Optional<T> pick() {
        OptionalBinding<T> binding;
        if (this.binding != null)
            binding = this.binding;
        else {
            Objects.requireNonNull(title, "Picker must have a title or a binding.");
            var sbinding = SiteFragmentBindings.bindOptionalString(title);
            binding = new OptionalBinding<T>() {
                @Override public Optional<T> get() { return sbinding.get().flatMap(s -> items.stream().filter(v -> naming.apply(v).equals(s)).findFirst()); }
                @Override public void set(Optional<T> value) { sbinding.set(value.map(naming)); }
            };
        }
        var bound = binding.get().orElse(null);
        var current = items.contains(bound) ? bound : null;
        new ContentLabel(title)
            .sidebar(sidebar)
            .add(Html.ul()
                .clazz("item-picker")
                .add(Html.li()
                    .clazz("item-picker-none", current == null ? "item-picker-current" : null)
                    .add(Html.button()
                        .id(SiteFragment.get().elementId(title, "empty"))
                        .onclick(() -> binding.set(Optional.empty()))
                        .add(Svg.svg()
                            .viewBox("-10 -10 20 20")
                            .add(Svg.line()
                                .x1(10)
                                .y1(-10)
                                .x2(-10)
                                .y2(10)
                                .stroke("black")
                                .strokeWidth(3))
                            .add(Svg.line()
                                .x1(-10)
                                .y1(-10)
                                .x2(10)
                                .y2(10)
                                .stroke("black")
                                .strokeWidth(3)))))
                .add(items.stream()
                    .map(v -> Html.li()
                        .clazz(v.equals(current) ? "item-picker-current" : null)
                        .add(Html.button()
                            .id(SiteFragment.get().elementId(title, "item", naming.apply(v)))
                            .onclick(() -> binding.set(Optional.of(v)))
                            .add(naming.apply(v))))))
            .render();
        return Optional.ofNullable(current);
    }
    @SafeVarargs public static <T> Optional<T> pick(String title, T... items) { return new OptionalPicker<T>().title(title).add(items).pick(); }
    public static <T> Optional<T> pick(String title, Collection<T> items, Function<T, String> naming) {
        return new OptionalPicker<T>().title(title).add(items).naming(naming).pick();
    }
    public static <T> Optional<T> pick(String title, Collection<T> items) { return new OptionalPicker<T>().title(title).add(items).pick(); }
    public static <T> Optional<T> pick(String title, Stream<T> items, Function<T, String> naming) {
        return new OptionalPicker<T>().title(title).add(items).naming(naming).pick();
    }
    public static <T> Optional<T> pick(String title, Stream<T> items) { return new OptionalPicker<T>().title(title).add(items).pick(); }
}
