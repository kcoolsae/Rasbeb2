Interactive tasks
===
{% include _menu.md %}

---

**Important note** Interactive tasks were not supported in Rasbeb 1. That means
that the specs below are probably prone to change.

There are different types of interactive task:
1. Tasks in which the interactivity simply serves to help the user to more easily find the correct answer. The question type
remains however 'standard': multiple choice, open integer or open text.
2. Tasks which provide an interactive answer type: draw lines between object, move objects into correct places, 
select the correct objects in a grid, ...
3. Like the previous example, but the interactive task does not produce an actual answer that can be correct or incorrect. Pupils
   only need to complete the task to score. 

The first type of interactive task seems the easiest to implement. At first sight, only the appropriate JavaScript needs to
be bundled in the question folder. Unfortunately, this is not sufficient: some data needs to be stored in the database
so that when a user returns to an interactive task, the state of that task is not 'forgotten'.
(The [Web storage API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Storage_API/Using_the_Web_Storage_API) may be an answer here.)

The other types require more work. Somehow what is entered on the interactive page must be forwarded as an answer to the
question in a format which the server can understand and can compare with the expected answer as entered by the author.
A JSON or XML format could be used for this, but checking whether the answer is correct might be more difficult.
* It must have a unique normalized form, so correctness can easily be checked (by the datbase)
* Authors will not be able to formulate the correct answer in JSON or XML format.

### Bebras Lodge?

We would like it to be possible to use the [Bebras Lodge](https://lodge.bebras.lt/) libraries to simplify building
interactive tasks. This is not trivial.
* Although the Lodge provides a way to produces a JSON-answer from what was entered by the user, that answer is not always normalized (order of the fields in an object may differ)
* As far as I can see, Lodge does not store state between tasks. When you return to a task, you have to start anew.

But surely Lodge can still be used to produce the interactive part of a question?