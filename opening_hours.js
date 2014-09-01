function opening_hours() {

    // Try to move the name member around (in the object) -> ClassCastException disappears.
    // Comment out any member of the object -> ClassCastException disappears.
    var other_object = {
        'name': 'test name',
        '2005': [],
        '2007': [],
        '2006': [],
    };

    var object = {
        'name': 'hello',
        // '2005': [],
        // '2007': [],
        // '2006': [],
    };

    // Comment out both -> ClassCastException disappears.
    object['name'];
    object.name;
    throw 'Test exception.';
}
