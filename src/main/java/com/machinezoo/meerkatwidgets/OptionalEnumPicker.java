// Part of Meerkat Widgets: https://meerkatwidgets.machinezoo.com
package com.machinezoo.meerkatwidgets;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import com.machinezoo.remorabindings.*;
import com.machinezoo.stagean.*;

/*
 * Variation of EnumPicker that allows empty Optional result.
 */
@DraftApi
public class OptionalEnumPicker<T extends Enum<T>> {
    private String title;
    public OptionalEnumPicker<T> title(String title) {
        this.title = title;
        return this;
    }
    /*
     * Either clazz or subset must to be provided.
     */
    private Class<T> clazz;
    public OptionalEnumPicker<T> clazz(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }
    private final List<T> items = new ArrayList<>();
    public OptionalEnumPicker<T> add(T item) {
        Objects.requireNonNull(item);
        if (!items.contains(item))
            items.add(item);
        return this;
    }
    public OptionalEnumPicker<T> add(Stream<? extends T> items) {
        items.forEach(this::add);
        return this;
    }
    public OptionalEnumPicker<T> add(Collection<? extends T> items) { return add(items.stream()); }
    @SafeVarargs public final OptionalEnumPicker<T> add(T... items) { return add(Arrays.stream(items)); }
    public OptionalEnumPicker() {}
    public OptionalEnumPicker(String title) { this.title = title; }
    public OptionalEnumPicker(String title, Class<T> clazz) {
        this.title = title;
        this.clazz = clazz;
    }
    public OptionalEnumPicker(String title, Collection<? extends T> items) {
        this.title = title;
        add(items);
    }
    public OptionalEnumPicker(String title, Stream<? extends T> items) {
        this.title = title;
        add(items);
    }
    @SafeVarargs public OptionalEnumPicker(String title, T... items) {
        this.title = title;
        add(items);
    }
    private OptionalBinding<T> binding;
    public OptionalEnumPicker<T> binding(OptionalBinding<T> binding) {
        this.binding = binding;
        return this;
    }
    private Function<T, String> naming = Object::toString;
    public OptionalEnumPicker<T> naming(Function<T, String> naming) {
        Objects.requireNonNull(naming);
        this.naming = naming;
        return this;
    }
    private boolean sidebar = true;
    public OptionalEnumPicker<T> sidebar(boolean sidebar) {
        this.sidebar = sidebar;
        return this;
    }
    @SuppressWarnings("unchecked") public Optional<T> pick() {
        if (clazz == null && items.isEmpty())
            throw new IllegalStateException("Enum type must be specified implicitly or explicitly.");
        var clazz = this.clazz != null ? this.clazz : (Class<T>)items.get(0).getClass();
        var items = !this.items.isEmpty() ? this.items : Arrays.asList(clazz.getEnumConstants());
        var binding = this.binding != null ? this.binding : SiteFragmentBindings.bindOptionalString(title).encodeEnum(clazz);
        return new OptionalPicker<T>(title, items)
            .binding(binding)
            .naming(naming)
            .sidebar(sidebar)
            .pick();
    }
    public static <T extends Enum<T>> Optional<T> pick(String title, Class<T> clazz) { return new OptionalEnumPicker<T>().title(title).clazz(clazz).pick(); }
    public static <T extends Enum<T>> Optional<T> pick(Class<T> clazz) { return new OptionalEnumPicker<T>().title(clazz.getSimpleName()).clazz(clazz).pick(); }
}
