<h1 align="center">Contributing to PlayerAccessories</h1>

In the following lines, you found a list of guidelines for the contribution to the PlayerAccessories
Addon. <br>
Those are mostly guidelines not rules!

[Code of Conduct](#code-of-conduct)

[I just have questions](#just-questions)

[How do i contribute?](#how-do-i-contribute)

- [Bug and Feature](#bug-and-feature)
  - [What is a good Bug-Issue?](#what-is-a-good-bug-issue)
  - [What is a good Feature-Issue?](#what-is-a-good-feature-issue)
- [First contribute to the code](#first-contribute-to-the-code)

[Code](#code)
- [Branch](#branch)
- [Pull Request](#pull-request)
- [Commit](#commit)

## Code of Conduct

This project and everyone involved is subject of the [Code of Conduct](CODE_OF_CONDUCT.md).
By participating, you are expected to employ with this Code! <br>
Please report unaccountable behaviour to the discord!

## Just Questions

Please don't create an issue for a question. Our team on discord help you always with your problem
our question!

## How do i Contribute

### Bug and Feature

If you want to report a bug or feature issue, please use our given templates in the issue tab!

Before you report a bug, please check whether it has not already been reported and possibly even worked on.
If you are reporting a bug, please collect as much information as you can and attach it to the issue.
If you find a closed issue where you think its addressed the same issue, please create a new issue and
link the old one.

### What is a good Bug-Issue?

- **Use a clear and descriptive title** for the issue to identify it.
- **Describe the exact steps that reproduce the problem** in as much detail as possible.
  - Start, for example, with whether the game took longer to start
  - or which command u used
  - If you list the steps, **don't just say what you did, but also explain how you did it!**
    - With hotkeys or manual input or something
- **Explain what behaviour you expected instead and why!**
- **Include screenshots and GIFs** showing how the error occurred and clearly identifying it.
  - You can use [this tool](https://www.cockos.com/licecap/) for creating GIFs under macOS and Windows
  - or [this tool](https://github.com/colinkeenan/silentcast) for Linux
- **If the game crashs**, attach a crash report. This is automatically opened by LabyMod
  - Add the crash report in a [Markdown-Codeblock](https://help.github.com/articles/markdown-basics/#multiple-lines)
  - a [file attachment](https://help.github.com/articles/file-attachments-on-issues-and-pull-requests/)
  - or an linked [Gist](https://gist.github.com)
- **If the issue was not caused by a specific action**, describe what you did before the problem has occurred

### What is a good Feature-Issue?

- **Use a clear and descriptive title** for the issue to identify it.
- **Provide a step-by-step description of the proposed improvement** with as much detail as possible
- **Give specific examples to demonstrate the steps.
  - Copy / Paste snippets you found using these examples as [Markdown-Codeblock](https://help.github.com/articles/markdown-basics/#multiple-lines)
- **Describe the current behavior** and **explain what behavior you expected instead** and why
- **Include screenshots and GIFs** showing how the feature will implement or the part of Indicate the PlayerAccessories Addon to which the suggestion relates.
  - You can use [this tool](https://www.cockos.com/licecap/) to create GIFs under macOS and Windows
  - or [this tool](https://github.com/colinkeenan/silentcast) for Linux
- **Explain why this extension would be useful for most of the users from PlayerAccessories.**

### First contribute to the code

For an easier entry into the project there are `good first issue` and `help wanted` labels:

- [good first issue](https://github.com/epycsolutions-addons/playeraccessories/labels/good%20first%20issue) - Issues that only need a few lines of code
- [help wanted](https://github.com/epycsolutions-addons/playeraccessories/labels/help%20wanted) - Issues that is something more demanding then `good first issue`

## Code

### Branch

The branch name results from the title of the issue. PAAB stands for bug and PAAF for feature.
Spaced become replaced by hyphens. The template for branches is then showed like this:

- (PAAF) Feature: `feature/PAAF NUMBER DESCRIPTION`
- (PAAB) Bug: `bug/PAAB NUMBER DESCRIPTION`

If the issue is called `PAAB - Fixed typo error` and has the number 10 (GitHub number), the result is
the following Branch: `bug/PAAB-10-fixed-typo-error`

### Pull request

There are a few guidelines for pull requests that are important for clear work.
The name of the Pull request is always the name of the [branch](#branch) to be merged.

If the branch is named `bug/PAAB-10-fixed-typo-error` then the name of the pull request is the exact same.

In the description of a pull request comes the issue that is thereby closed.
In this case, it would be Description:

> NOTE: [Closing Keywords](https://docs.github.com/articles/closing-issues-using-keywords) are used!

```
closes #10
```

> Attention: since an issue has to be linked, there is an issue for everything. Working without issue is
> undesirable. Issues can als be created after the actual coding. <br>
> However, there is no right to that merging submitted pull requests!

### Commits

Commit messages can be designed in any way.

The following is requests:
- No emoji